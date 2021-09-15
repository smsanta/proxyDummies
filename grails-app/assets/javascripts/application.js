// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better
// to create separate JavaScript files as needed.
//
//= require jquery-3.3.1.min
//= require bootstrap
//= require bootstrap.bundle

//Application Resources
//Abstract Resources
//= require app/abstract/DummyObject
//= require app/abstract/AbstractFilter

//Domains
//= require app/domain/Rule
//= require app/domain/Environment

//Filter Resources
//= require app/filters/rule/SearchRuleFilter
//= require app/filters/logs/SearchRequestLogFilter

// Static Resources
//= require app/statics/httpStatus
//= require app/statics/htmlTemplates
//= require app/statics/htmlGenerator
//
// Utility Resources
//= require app/utils/recurrent/Recurrent
//= require app/utils/recurrent/Timeout
//= require app/utils/recurrent/Interval

//= require app/utils
//= require app/validators
//= require app/config
//= require app/comunicator
//= require app/apiClient
//= require app/modals
//
// Views
//= require app/views/rule/dashboard.js
//= require app/views/rule/saveRule.js
//= require app/views/environment/saveEnvironment.js
//= require app/views/configuration/configuration.js
//= require app/views/request/logs.js

//Main App Resources
//= require app/apiClient
//= require app/app

//= require_self
