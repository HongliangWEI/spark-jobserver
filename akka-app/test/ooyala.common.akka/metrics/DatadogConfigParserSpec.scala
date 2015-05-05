package ooyala.common.akka.metrics

import com.typesafe.config.ConfigFactory
import java.net.InetAddress
import org.scalatest.{FunSpec, Matchers}

class DatadogConfigParserSpec extends FunSpec with Matchers {

  describe("parses Datadog config") {
    it("should parse a valid config") {
      val configStr =
        """
        spark.jobserver.metrics.datadog {
          hostname = "test"
          apikey = "12345"
          duration = 10
        }
        """
      val config = ConfigFactory.parseString(configStr)
      val datadogConfig = DatadogConfigParser.parse(config)

      val hostName = datadogConfig.hostName
      hostName.isDefined should equal(true)
      hostName.get should equal("test")

      val apiKey = datadogConfig.apiKey
      apiKey.isDefined should equal(true)
      apiKey.get should equal("12345")

      datadogConfig.durationInSeconds should equal(10L)
    }

    it("should return local host name and an api key") {
      // Omits host name
      val configStr =
        """
        spark.jobserver.metrics.datadog {
          apikey = "12345"
          duration = 10
        }
        """

      val config = ConfigFactory.parseString(configStr)
      val datadogConfig = DatadogConfigParser.parse(config)

      val hostName = datadogConfig.hostName
      hostName.isDefined should equal(true)
      // When host name isn't defined in config file, local host name should be returned
      hostName.get should equal(InetAddress.getLocalHost.getCanonicalHostName)

      val apiKey = datadogConfig.apiKey
      apiKey.isDefined should equal(true)
      apiKey.get should equal("12345")

      datadogConfig.durationInSeconds should equal(10L)
    }

    it ("should return only local host name") {
      // Omits host name and api key
      val configStr =
        """
        spark.jobserver.metrics.datadog {
        }
        """

      val config = ConfigFactory.parseString(configStr)
      val datadogConfig = DatadogConfigParser.parse(config)

      val hostName = datadogConfig.hostName
      hostName.isDefined should equal(true)
      hostName.get should equal(InetAddress.getLocalHost.getCanonicalHostName)

      datadogConfig.apiKey.isDefined should equal(false)
    }

    it ("should parse a valid list of tags") {
      val configStr =
        """
        spark.jobserver.metrics.datadog {
          tags = ["test version", "has tags"],
        }
        """
      val config = ConfigFactory.parseString(configStr)
      val datadogConfig = DatadogConfigParser.parse(config)

      val tags = datadogConfig.tags
      tags.isDefined should equal(true)
      tags.get should equal(List("test version", "has tags"))
    }

    it ("should parse no tags") {
      val configStr =
        """
        spark.jobserver.metrics.datadog {
        }
        """
      val config = ConfigFactory.parseString(configStr)
      val datadogConfig = DatadogConfigParser.parse(config)

      val tags = datadogConfig.tags
      tags.isDefined should equal(false)
    }

    it ("should parse a valid datadog agent port") {
      val configStr =
        """
        spark.jobserver.metrics.datadog {
          agentport = 9999
        }
        """
      val config = ConfigFactory.parseString(configStr)
      val datadogConfig = DatadogConfigParser.parse(config)

      val agentPort = datadogConfig.agentPort
      agentPort.isDefined should equal(true)
      agentPort.get should equal(9999)
    }

    it ("should parse no datadog agent port") {
      val configStr =
        """
        spark.jobserver.metrics.datadog {
        }
        """
      val config = ConfigFactory.parseString(configStr)
      val datadogConfig = DatadogConfigParser.parse(config)

      val agentPort = datadogConfig.agentPort
      agentPort.isDefined should equal(false)
    }
  }
}