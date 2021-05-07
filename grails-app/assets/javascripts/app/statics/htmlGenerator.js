var htmlGenerator = {

    temaplates: {
        tag: '<___TAG___></___TAG___>'
    },

    tag: {
        icon: function (attributtes, css, plainString) {
            var newIcon = htmlGenerator.tag._createElement("i", attributtes);

            if (validator.isObject(plainString)) {
                return newIcon;
            } else {
                return newIcon.prop('outerHTML');
            }
        },

        span: function (attributtes, css, plainString) {
            var newSpan = htmlGenerator.tag._createElement("span", attributtes);

            if (validator.isObject(plainString)) {
                return newSpan;
            } else {
                return newSpan.prop('outerHTML');
            }
        },

        _createElement: function (tag, attributtes, css) {
            var tagTemplate = htmlGenerator.temaplates.tag;
            tagTemplate = tagTemplate.replaceAll('___TAG___', tag);

            var newElement = $(tagTemplate);

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
            var base = {
                "data-bs-toggle": "tooltip",
                "data-bs-placement": "top",
                "title": tooltip
            };

            $.extend(base, attrs);

            var element = htmlGenerator.tag._createElement("i", base);
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