package com.mogobiz.mockserver.mirakl.conf

import com.typesafe.config.{Config, ConfigFactory}

/**
  *
  * Created by smanciot on 10/09/16.
  */
object Settings {

  lazy val config: Config = ConfigFactory.load("mockserver").withFallback(ConfigFactory.load("default-mirakl"))

  val RootPath = config getString "mirakl.rootPath"
  val Active = config getBoolean "mirakl.active"

  object Mirakl {
    val remote = Some(
      Remote(
        config getInt "mirakl.remote.port",
        config getString "mirakl.remote.host",
        config getBoolean "mirakl.remote.secure"
      )
    )
    val frontApikey = config getString "mirakl.frontApiKey"
  }

}
