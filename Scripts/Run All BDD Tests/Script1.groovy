import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import internal.GlobalVariable

// Run all BDD feature files
CucumberKW.runFeatureFolder('Include/features')

WebUI.comment('All BDD feature files executed.')
