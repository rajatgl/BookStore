package com.bridgelabz.bookstore.database.mysql.configurations

case class MySqlConnection(server: String = "localhost", name: String = "root") {
  require(server != null, "DB Server parameter is null")
  require(name != null, "DB (user) name parameter is null")

  def getConnectionString: String =
    "jdbc:mysql://%s:3306/bookstore?user=%s".
      format(server, name)
}
