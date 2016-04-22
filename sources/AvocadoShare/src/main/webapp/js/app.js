'use strict'

function insertAfter(newNode, referenceNode) {
    referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}

var Autocomplete = function (input) {
    this.input = input;
    var that = this;
    input.addEventListener("keyup", function (evt) {
        that.updateSuggestions(this.value);
    });
    this.hiddenInput = document.createElement("input");
    this.hiddenInput.setAttribute("type", "hidden");
    this.url = input.getAttribute("data-suggestion");
    this.hiddenInput.name = input.name;
    input.name = "";
    this.container = document.createElement("ul");
    this.container.className = "suggestion-container dropdown-menu";
    insertAfter(this.container, input);
    insertAfter(this.hiddenInput, input);
};

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
    this.hiddenInput.value = element.getAttribute("data-suggestion-id");
    this.input.value = element.innerText;
    this.showResults([]);
}

Autocomplete.prototype.showResults = function (users) {
    this.container.innerHTML = "";
    if (users.length == 0) {
        this.container.style.display = "none";
    } else {
        this.container.style.display = "block";
        for (var i = 0; i < users.length; i++) {
            var element = document.createElement("li");
            element.className = "dropdown-item";
            var that = this;
            $(element).click(function () {
                that.selectElement(this);
            });
            element.setAttribute("data-suggestion-id", users[i].id);
            element.innerText = users[i].name;
            this.container.appendChild(element);
        }
    }
};
