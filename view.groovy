listView('testlist1') {
  description('All new jobs for testlist')
  filterBuildQueue()
  filterExecutors()
  jobs {
    name('print_credential')
    name('greetingJob3')

  }
    columns {
      status()
      weather()
      name()
      lastSuccess()
      lastFailure()
      lastDuration()
      buildButton()
    }
}
