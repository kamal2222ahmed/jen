reportUrl = manager.getEnvVariable("BUILD_URL") + "artifact/" 
manager.addBadge("text.gif", "View logs", reportUrl + "Apps/Tests/Serenity/target/site/serenity/logs") 
manager.addBadge("db_out.gif", "Video Recording (View in Firefox, or save locally and view in Chrome or Windows Media Player)", reportUrl + "Apps/Tests/Serenity/target/site/serenity/serenity-recording.mp4")
if(manager.logContains(".*Generating Serenity Reports.*")){ 
    manager.addBadge("folder-open.gif", "View Report", reportUrl + "Apps/Tests/Serenity/target/site/serenity/index.html") 
    manager.addBadge("save.gif", "Download Report", reportUrl + "*zip*/archive.zip") 
}
