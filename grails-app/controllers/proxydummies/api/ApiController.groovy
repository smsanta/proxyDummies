package proxydummies.api

import io.micronaut.http.HttpMethod
import proxydummies.Configuration
import proxydummies.Environment
import proxydummies.ProxyService
import proxydummies.Rule
import proxydummies.SystemConfigsService
import proxydummies.abstracts.ApiBaseController
import proxydummies.command.DeleteCommand
import proxydummies.command.IdCommand
import proxydummies.command.configuration.ConfigurationKeyCommand
import proxydummies.command.configuration.UpdateConfigurationCommand
import proxydummies.command.environment.SaveEnvironmentCommand
import proxydummies.command.rule.CreateRuleCommand
import proxydummies.command.rule.ImportRuleCommand
import proxydummies.command.rule.UpdateRuleCommand
import proxydummies.filters.FilterResult
import proxydummies.filters.RuleFilter
import proxydummies.filters.EnvironmentFilter

class ApiController extends ApiBaseController{

    ProxyService proxyService
    SystemConfigsService systemConfigsService

    def searchRules(){
        handle{
            def filter = RuleFilter.newInstance( populate: getRequestParams() )
            info(filter)

            FilterResult fResult = proxyService.searchRule( filter )

            def items = fResult.toMapObject()

            respondOK( items )
        }
    }

    def createRule(){
        handle{
            CreateRuleCommand ruleCommand = getCommandAndValidate( CreateRuleCommand.newInstance(), HttpMethod.POST )

            Rule newRule = proxyService.saveRule(
                ruleCommand.uri,
                ruleCommand.priority,
                ruleCommand.sourceType,
                ruleCommand.data,
                ruleCommand.active,
                ruleCommand.description,
                ruleCommand.requestConditionActive,
                ruleCommand.requestCondition,
                ruleCommand.method,
                ruleCommand.serviceType,
                ruleCommand.responseStatus,
                ruleCommand.responseExtraHeaders,
            )

            respondOK( newRule.toMapObject() )
        }
    }

    def updateRule(){
        handle{
            UpdateRuleCommand ruleCommand = getCommandAndValidate( UpdateRuleCommand.newInstance(), HttpMethod.POST )

            Rule updatedRule = proxyService.saveRule(
                ruleCommand.uri,
                ruleCommand.priority,
                ruleCommand.sourceType,
                ruleCommand.data,
                ruleCommand.active,
                ruleCommand.description,
                ruleCommand.requestConditionActive,
                ruleCommand.requestCondition,
                ruleCommand.method,
                ruleCommand.serviceType,
                ruleCommand.responseStatus,
                ruleCommand.responseExtraHeaders,
                ruleCommand.id
            )

            respondOK( updatedRule.toMapObject() )
        }
    }

    def deleteRule(){
        handle{
            DeleteCommand delCommand = getCommandAndValidate( DeleteCommand.newInstance(), HttpMethod.POST )

            proxyService.deleteRule( delCommand.id )

            respondOK( "Rule Deleted" )
        }
    }

    def enableRule(){
        handle {
            Rule updatedRule = changeRuleState(true )
            respondOK( updatedRule.toMapObject() )
        }
    }

    def disableRule(){
        handle {
            Rule updatedRule = changeRuleState(false )
            respondOK( updatedRule.toMapObject() )
        }
    }

    def getRuleDatabaseBody(){
        handle{
            IdCommand idCommand = getCommandAndValidate( IdCommand.newInstance(), HttpMethod.GET )

            FilterResult fResult = proxyService.searchRule( RuleFilter.newInstance( [id: idCommand.id] ) )

            Rule rule = fResult.results.first()

            String responseData = proxyService.loadDummy( rule )
            respondOK( responseData )
        }
    }

    def updateConfiguration() {
        handle{
            UpdateConfigurationCommand uConfigCommand = getCommandAndValidate( UpdateConfigurationCommand.newInstance(), HttpMethod.POST )

            Configuration updatedConfiguration = systemConfigsService.updateConfig( uConfigCommand.key, uConfigCommand.value, uConfigCommand.description, uConfigCommand.title )

            respondOK( updatedConfiguration.toMapObject() )
        }
    }

    def getConfiguration() {
        handle{
            ConfigurationKeyCommand uConfigCommand = getCommandAndValidate( ConfigurationKeyCommand.newInstance(), HttpMethod.POST )

            String configurationValue = systemConfigsService.getConfigurationValueByKey( uConfigCommand.key )

            respondOK( configurationValue )
        }
    }

    def exportRule(){
        handle{
            IdCommand idCommand = getCommandAndValidate( IdCommand.newInstance(), HttpMethod.GET )

            def exportedRule = proxyService.exportRule( idCommand.id )

            respondOK( exportedRule )
        }
    }

    def importRule(){
        handle{
            ImportRuleCommand importRuleCommand = getCommandAndValidate( ImportRuleCommand.newInstance(), HttpMethod.POST )

            Rule importedRule = proxyService.importRule(
                importRuleCommand.uri,
                importRuleCommand.data,
                importRuleCommand.description,
                importRuleCommand.requestConditionActive,
                importRuleCommand.requestCondition,
                importRuleCommand.method,
                importRuleCommand.serviceType,
                importRuleCommand.responseStatus,
                importRuleCommand.responseExtraHeaders
            )

            respondOK( importedRule.toMapObject() )
        }
    }

    def searchEnvironment(){
        handle{
            def filter = EnvironmentFilter.newInstance( populate: getRequestParams() )
            info(filter)

            FilterResult fResult = proxyService.searchEnvironment( filter )

            def items = fResult.toMapObject()

            respondOK( items )
        }
    }

    def saveEnvironment(){
        handle{
            SaveEnvironmentCommand environmentCommand = getCommandAndValidate( SaveEnvironmentCommand.newInstance(), HttpMethod.POST )

            Environment updateEnvironment = proxyService.saveEnvironment(
                environmentCommand.name,
                environmentCommand.url,
                environmentCommand.uriPrefix,
                environmentCommand.id
            )

            respondOK( updateEnvironment.toMapObject() )
        }
    }

    def deleteEnvironment(){
        handle{
            DeleteCommand delCommand = getCommandAndValidate( DeleteCommand.newInstance(), HttpMethod.POST )

            proxyService.deleteEnvironment( delCommand.id )

            respondOK( "Environmet Deleted" )
        }
    }

    //Non endpoint Methods ---------------------------------------------------------------------------------------------
    private Rule changeRuleState(Boolean newState){
        IdCommand switchCommand = getCommandAndValidate( IdCommand.newInstance(), HttpMethod.POST )

        Rule updatedRule = proxyService.changeRuleState( switchCommand.id, newState )

        updatedRule
    }
}
