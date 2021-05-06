package proxydummies.api

import io.micronaut.http.HttpMethod
import proxydummies.Configuration
import proxydummies.ProxyService
import proxydummies.Rule
import proxydummies.SystemConfigsService
import proxydummies.abstracts.ApiBaseController
import proxydummies.command.DeleteCommand
import proxydummies.command.IdCommand
import proxydummies.command.configuration.ConfigurationKeyCommand
import proxydummies.command.configuration.UpdateConfigurationCommand
import proxydummies.command.rule.CreateRuleCommand
import proxydummies.command.rule.ImportRuleCommand
import proxydummies.command.rule.UpdateRuleCommand
import proxydummies.filters.FilterResult
import proxydummies.filters.RuleFilter

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
                ruleCommand.requestCondition
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

            respondOK( rule.data )
        }
    }

    def updateConfiguration() {
        handle{
            UpdateConfigurationCommand uConfigCommand = getCommandAndValidate( UpdateConfigurationCommand.newInstance(), HttpMethod.POST )

            Configuration updatedConfiguration = systemConfigsService.updateConfig( uConfigCommand.key, uConfigCommand.value )

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
                importRuleCommand.requestCondition
            )

            respondOK( importedRule.toMapObject() )
        }
    }

    private Rule changeRuleState(Boolean newState){
        IdCommand switchCommand = getCommandAndValidate( IdCommand.newInstance(), HttpMethod.POST )

        Rule updatedRule = proxyService.changeRuleState( switchCommand.id, newState )

        updatedRule
    }
}
