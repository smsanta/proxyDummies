let htmlGenerator = {

    templates: {
        tag: '<___TAG___></___TAG___>'
    },

    tag: {
        icon: function (attributtes, css, plainString) {
            let newIcon = htmlGenerator.tag._createElement("i", attributtes, css);

            if (validator.isObject(plainString)) {
                return newIcon;
            } else {
                return newIcon.prop('outerHTML');
            }
        },

        span: function (attributtes, css, plainString) {
            let newSpan = htmlGenerator.tag._createElement("span", attributtes, css);

            if (validator.isObject(plainString)) {
                return newSpan;
            } else {
                return newSpan.prop('outerHTML');
            }
        },

        textarea: function (attributtes, css, plainString) {
            let newSpan = htmlGenerator.tag._createElement("textarea", attributtes, css);

            if (validator.isObject(plainString)) {
                return newSpan;
            } else {
                return newSpan.prop('outerHTML');
            }
        },

        a: function (attributtes, css, plainString) {
            let newSpan = htmlGenerator.tag._createElement("a", attributtes, css);

            if (validator.isObject(plainString)) {
                return newSpan;
            } else {
                return newSpan.prop('outerHTML');
            }
        },

        _createElement: function (tag, attributtes, css) {
            let tagTemplate = htmlGenerator.templates.tag;
            tagTemplate = tagTemplate.replaceAll('___TAG___', tag);

            let newElement = $(tagTemplate);

            if (validator.isObject(attributtes)) {
                $.each(attributtes, function (key, value) {
                    if (key != "text") {
                        newElement.attr(key, value);
                    }
                })
            }

            if (validator.isObject(css)) {
                $.each(css, function (key, value) {
                    newElement.css(key, value);
                })
            }

            if (validator.isObject(attributtes.text)) {
                newElement.append(attributtes.text);
            }

            return newElement
        }
    },

    icons: {
        any: function(iconClass, tooltip, attrs, plainString){
            let base = {
                "data-bs-toggle": "tooltip",
                "data-bs-placement": "top",
                "title": tooltip
            };

            $.extend(base, attrs);

            let element = htmlGenerator.tag._createElement("i", base);
            element.addClass(iconClass);

            if (validator.isObject(plainString)) {
                return element;
            } else {
                return element.prop('outerHTML');
            }
        },

        info: function (colorClass, tooltip, attrs, plainString) {
            colorClass = validator.getValueOrDefault(colorClass, "");
            return htmlGenerator.icons.any( ("bi-info-circle-fill action-icon" + colorClass) , tooltip, attrs, plainString )
        },

        eyeFill: function (colorClass, tooltip, attrs, plainString) {
            colorClass = validator.getValueOrDefault(colorClass, "");
            return htmlGenerator.icons.any(("bi-eye-fill action-icon " + colorClass), tooltip, attrs, plainString )
        },

        server: function (colorClass, tooltip, attrs, plainString) {
            colorClass = validator.getValueOrDefault(colorClass, "");
            return htmlGenerator.icons.any(("bi-server action-icon " + colorClass), tooltip, attrs, plainString )
        },

        fileCode: function (colorClass, tooltip, attrs, plainString) {
            colorClass = validator.getValueOrDefault(colorClass, "");
            return htmlGenerator.icons.any(("bi-file-earmark-code action-icon " + colorClass), tooltip, attrs, plainString )
        },

        okayCheck: function (colorClass, tooltip, attrs, plainString) {
            colorClass = validator.getValueOrDefault(colorClass, "");
            return htmlGenerator.icons.any(("check-circle-fill action-icon " + colorClass), tooltip, attrs, plainString )
        },
    }
}