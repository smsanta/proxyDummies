var templater = {
    getFilledTemplate: function (data, template) {

        let filledTemplate = template;
        $.each(data, function (key, value) {
            filledTemplate = filledTemplate.replaceAll(key, value)
        });

        return filledTemplate;
    },

    TEMPLATE_RULE_TABLE_ROW:
        '<tr data-id="__ID__">' +
            '<td data-uri="__URI__"> <i data-description="__DESCRIPTION__" class="bi-info-circle-fill" style="color: #0a53be" data-bs-toggle="tooltip" data-bs-placement="top" title="__DESCRIPTION__"></i> __URI__ </td>' +
            '<td data-active="__ACTIVE__">__ACTIVE_STRING__</td>' +
            '<td data-priority="__PRIORITY__">__PRIORITY__</td>' +
            '<td data-sourceType="__TYPE__">__TYPE__</td>' +
            '<td data-data="__DATA__">__DATA__</td>' +
            '<td class="table-actions">' +
                '<i id="edit-__ID__" class="bi-pencil-square" action="edit" style="color: dodgerblue;"></i>' +
                '<i id="activate-__ID__"class="bi-hand-thumbs-up-fill" style="color: green;" action="activate"></i>' +
                '<i id="deactivate-__ID__"class="bi-hand-thumbs-down-fill" style="color: orange;" action="deactivate"></i>' +
                '<i id="delete-__ID__"class="bi-x-circle" style="color: red;" action="delete"></i>' +
            '</td>' +
        '</tr>',

    TEMPLATE_RULE_TABLE_ROW_EMPTY: '<td colspan="6" class="no-results-row">No hay resultados!</td>'
};

