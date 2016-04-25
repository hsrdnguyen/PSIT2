"use strict";

function insertAfter(newNode, referenceNode) {
    referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}

function Autocomplete(input) {
    if (typeof input === 'undefined' || input === null) {
        throw new TypeError("Invalid input");
    }
    this.input = input;
    var that = this;
    input.addEventListener("keyup", function (evt) {
        that.updateSuggestions(this.value);
    });
    this.hiddenInput = document.createElement("input");
    this.suggestionValueAttribute = "data-suggestion-id";
    this.hiddenInput.setAttribute("type", "hidden");
    this.cachedSuggestions = null;
    this.url = input.getAttribute("data-suggestion");
    this.input.setAttribute("autocomplete", "off");
    this.hiddenInput.name = input.name;
    input.name = "";
    this.container = document.createElement("ul");
    this.container.className = "suggestion-container dropdown-menu";
    insertAfter(this.container, input);
    insertAfter(this.hiddenInput, input);
}

Autocomplete.prototype.updateSuggestions = function (query) {
    query = query.trim();
    if (query === this.cachedSuggestions) {
        this.showCachedResult();
    } else {
        if (query.length == 0) {
            this.hideResults();
        } else {
            $.getJSON(this.url + "?q=" + encodeURIComponent(query),
                function (query, obj) {
                    return function (data) {
                        obj.showResults(query, data["users"]);
                    };
                }(query, this)
            );
        }
    }
};

Autocomplete.prototype.selectElement = function (element) {
    console.log(element);
    this.hiddenInput.value = element.getAttribute(this.suggestionValueAttribute);
    this.input.value = element.innerText;
    this.hideResults();
};

Autocomplete.prototype.hideResults = function () {
    this.container.style.display = "none";
};

Autocomplete.prototype.showCachedResult = function () {
    this.container.style.display = "block";
};

Autocomplete.prototype.showResults = function (query, users) {
    if (users.length == 0) {
        this.hideResults();
    } else {
        this.container.innerHTML = "";
        this.cachedSuggestions = "";
        this.container.style.display = "block";
        for (var i = 0; i < users.length; i++) {
            var element = document.createElement("li");
            element.className = "dropdown-item";
            element.addEventListener("click",
                function (obj) {
                    return function () {
                        obj.selectElement(this);
                    }
                }(this)
            );
            element.setAttribute(this.suggestionValueAttribute, users[i].id);
            element.innerText = users[i].name;
            this.container.appendChild(element);
        }
        this.cachedSuggestions = query;
    }
};
