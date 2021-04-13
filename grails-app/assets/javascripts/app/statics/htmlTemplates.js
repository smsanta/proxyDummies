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
            '<td class="t-align-center">__DATA_STRING__</td>' +
            '<td class="t-align-center">__REQUEST_CONDITION__</td>' +
            '<td class="table-actions t-align-center">' +
                '<i id="edit-__ID__" class="bi-pencil-square action-icon" action="edit" style="color: dodgerblue;" data-bs-toggle="tooltip" data-bs-placement="top" data-original-title="Editar Rule"></i>' +
                '<i id="activate-__ID__"class="bi-hand-thumbs-up-fill action-icon" style="color: green;" action="activate" data-bs-toggle="tooltip" data-bs-placement="top" data-original-title="Activar Rule"></i>' +
                '<i id="deactivate-__ID__"class="bi-hand-thumbs-down-fill action-icon" style="color: orange;" action="deactivate" data-bs-toggle="tooltip" data-bs-placement="top" data-original-title="Desactivar Rule"></i>' +
                '<i id="export-__ID__"class="bi-share-fill action-icon" style="color: blueviolet;" action="export" data-bs-toggle="tooltip" data-bs-placement="top" data-original-title="Exportar Rule"></i>' +
                '<i id="delete-__ID__"class="bi-trash-fill action-icon" style="color: red; margin-left: 10px;" action="delete" data-bs-toggle="tooltip" data-bs-placement="top" data-original-title="Eliminar Rule"></i>' +
            '</td>' +
        '</tr>',

    TEMPLATE_RULE_TABLE_ROW_EMPTY: '<td colspan="6" class="no-results-row">No hay resultados!</td>',
    TEMPLATE_RULE_TABLE_DATA_TD_ICON_SHOW: '<i id="watch-__ID__" data-id="__ID__" class="bi-eye-fill action-icon" style="color: #1000ff;" action="watch" data-bs-toggle="tooltip" data-bs-placement="top" title="__TOOLTIP_TITLE__"></i>'
};

