"use strict";

function insertAfter(newNode, referenceNode) {
    referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}

function Autocomplete(input) {
    if(typeof input === 'undefined' || input === null) {
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
    if (query.length == 0) {
        this.showResults([]);
    } else {
        var that = this;
        $.getJSON(this.url + "?q=" + encodeURIComponent(query), function (data) {
            console.log(data.users);
            that.showResults(data.users);
        });
    }
};

Autocomplete.prototype.selectElement = function (element) {
    console.log(element);
    this.hiddenInput.value = element.getAttribute(this.suggestionValueAttribute);
    this.input.value = element.innerText;
    this.showResults([]);
};

Autocomplete.prototype.showResults = function (users) {
    this.container.innerHTML = "";
    if (users.length == 0) {
        this.container.style.display = "none";
    } else {
        this.container.style.display = "block";
        for (var i = 0; i < users.length; i++) {
            var element = document.createElement("li");
            element.className = "dropdown-item";
            element.addEventListener("click",
                function(obj){
                    return function () {
                        obj.selectElement(this);
                    }
                }(this)
            );
            element.setAttribute(this.suggestionValueAttribute, users[i].id);
            element.innerText = users[i].name;
            this.container.appendChild(element);
        }
    }
};
