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
            '<td> __DESCRIPTION__ __URI__ </td>' +
            '<td class="t-align-center">__ACTIVE_STRING__</td>' +
            '<td class="t-align-center">__PRIORITY__</td>' +
            '<td class="t-align-center">__TYPE__</td>' +
            '<td>__DATA_STRING__</td>' +
            '<td class="t-align-center">__REQUEST_CONDITION__</td>' +
            '<td class="table-actions">' +
                '<i id="edit-__ID__" class="bi-pencil-square action-icon" action="edit" style="color: dodgerblue;"></i>' +
                '<i id="activate-__ID__"class="bi-hand-thumbs-up-fill action-icon" style="color: green;" action="activate"></i>' +
                '<i id="deactivate-__ID__"class="bi-hand-thumbs-down-fill action-icon" style="color: orange;" action="deactivate"></i>' +
                '<i id="delete-__ID__"class="bi-x-circle action-icon" style="color: red;" action="delete"></i>' +
            '</td>' +
        '</tr>',

    TEMPLATE_RULE_TABLE_ROW_EMPTY: '<td colspan="6" class="no-results-row">No hay resultados!</td>',
    TEMPLATE_RULE_TABLE_DATA_TD_ICON_SHOW: '<i id="watch-__ID__" data-id="__ID__" class="bi-eye-fill action-icon" style="color: #1000ff;" action="watch" data-bs-toggle="tooltip" data-bs-placement="top" title="__TOOLTIP_TITLE__"></i>'
};

