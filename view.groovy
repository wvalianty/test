listView('testlist1') {
  description('All new jobs for testlist')
  filterBuildQueue()
  filterExecutors()
  jobs {
    name('print_credential')

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
