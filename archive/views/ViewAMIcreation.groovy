sectionedView('AMI + S3 Properties') {
    description('<h1><center> AMI creation and S3 properties upload jobs </center></h1>')
    sections {
        listView {
           name('AMI + S3 Properties')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('AMI.*')
                names('PROPERTIES_CREATION_to_S3')
            }
            columns {
                status()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
    }
}
