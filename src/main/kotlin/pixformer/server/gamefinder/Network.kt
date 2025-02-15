package pixformer.server.gamefinder

import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * Utility class for network operations.
 */
object Network {
    /**
     * Returns the local IP address of the machine, if available.
     */
    fun getIp(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces() ?: return null
        for (networkInterface in interfaces) {
            // Skip inactive interfaces or loopback interfaces if necessary.
            if (!networkInterface.isUp || networkInterface.isLoopback) continue

            val addresses = networkInterface.inetAddresses
            for (address in addresses) {
                // Check if the address is IPv4 and not a loopback address
                if (address is Inet4Address && !address.isLoopbackAddress) {
                    return address.hostAddress
                }
            }
        }
        return null
    }
}
