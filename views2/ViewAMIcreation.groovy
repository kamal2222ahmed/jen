sectionedView('AMI CREATION + S3') {
    description('<h1 style="text-align:center;"><strong><font size="5" color="red">AMI creation and S3 upload jobs</font><strong></h1>')
    sections {
        listView {
           name('AMI Release Candidate Creation')
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
