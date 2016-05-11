"use strict";

function Alert(containerId) {
    var container = document.getElementById(containerId);
    console.log(container);
    if(container === null || typeof container === 'undefined') {
        throw new Error("js error container not found!");
    }
    this.container = container;
}

Alert.addError = function(title, message) {
    this._addAlertBox("error", title, message);
};

Alert._addAlertBox = function(level, title, message) {
    var alert = document.createElement("div");
    alert.className = "alert alert-dismissible alert-dismissible fade in alert-" + level;
    alert.setAttribute("role", "alert");
    var content = '<button type="button" class="close" data-dismiss="alert" aria-label="Close">';
    content += '<span aria-hidden="true">&times;</span></button><strong>';
    content += title + '</strong>' + message + '</div>';
    alert.innerHTML = content;
    this.container.appendChild(alert);
};

var alert = null;


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

var Rating = function (container) {
    this.minRating = 1;
    this.maxRating = 4;
    this.container = container;
    this.objectId = container.getAttribute("data-rating-object");
    this.url = container.getAttribute("data-rating-url");

    this.container.className += " rating";


    var ratingButtonContainer = document.createElement("div");
    var ratingOverlay = document.createElement("div");
    ratingButtonContainer.className = "rating-buttons";
    ratingOverlay.className = "rating-overlay";
    /* ☆ ★ */
    for (var i = this.minRating; i <= this.maxRating; ++i) {
        var star = document.createElement("a");
        star.innerHTML = "★";
        star.addEventListener("click", function (rating, ratingObject) {
            return function() {
                var sendData = {rating: rating, object: ratingObject.objectId, method: "put"};
                $.ajax({
                    type: "POST",
                    url: ratingObject.url,
                    data: sendData,
                    success: function(data) {
                        console.log("Rating data: ", data);
                        ratingObject.setRating(data["rating"]);
                    }
                });
            }
        }(i, this));
        ratingButtonContainer.appendChild(star);
        star = document.createElement("span");
        star.innerHTML = "★";
        ratingOverlay.appendChild(star);
    }
    this.container.appendChild(ratingButtonContainer);
    this.container.appendChild(ratingOverlay);
};


Rating.prototype.setRating = function(rating) {
    var overlay = this.getRatingOverlay();
    var percents = 100.0 * rating / this.maxRating;
    overlay.style.width = percents + "%";
};

Rating.prototype.getRatingOverlay = function() {
    return this.container.childNodes.item(1);
};

Rating.prototype.getRatingButtonContainer = function() {
    return this.container.childNodes.item(0);
};

$(function () {
    $("[data-rating-object]").each(function(){
        new Rating(this);
    })
});


function SearchResult(apiUrl, id, type, element, requestUrl) {
    this.requestUrl = requestUrl;
    this.apiUrl = apiUrl;
    this.id = id;
    this.type = type;
    this.element = element;
    this._getRights();
}

SearchResult.prototype._getRights = function() {
    var promise = $.getJSON(this.apiUrl + this.id);
    promise.then(function(result){
        return function(data) {
            result.setAccess(data["access"]);
        }
    }(this));
};


SearchResult.prototype.setAccess = function(access){
    if(access == "NONE") {
        var button = document.createElement("button");
        button.className = "btn btn-default pull-xs-right";
        button.innerHTML = "Zugriff beantragen";
        button.addEventListener("click", function(obj){
            return function(evt) {
                evt.preventDefault();
                obj.requestAccess();
            }
        }(this));
        this.element.insertBefore(button, this.element.firstChild);
    }
};


SearchResult.prototype.requestAccess = function() {
    window.location.href = this.requestUrl + "?id=" + encodeURIComponent(this.id) + "&type=" + encodeURIComponent(this.type);
};

$(function(){
    alert = new Alert("js-error-container");
});