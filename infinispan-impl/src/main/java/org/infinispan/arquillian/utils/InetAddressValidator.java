package org.infinispan.arquillian.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Validator for IPv4 and IPv6 inet addresses
 *
 * @author <a href="mailto:vchepeli@redhat.com">Vitalii Chepeliuk</a>
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * @since Jun-2013
 */
public class InetAddressValidator
{

   /**
    * Validates an IPv4 address. Returns true if valid.
    *
    * @param inet4Address the IPv4 address to validate
    * @return true if the argument contains a valid IPv4 address
    */
   public static boolean isValidInet4Address(String inet4Address)
   {
      try
      {
         return InetAddress.getByName(inet4Address) instanceof Inet4Address;
      }
      catch (final UnknownHostException ex)
      {
         return false;
      }
   }

   /**
    * Validates an IPv6 address. Returns true if valid.
    *
    * @param inet6Address the IPv6 address to validate
    * @return true if the argument contains a valid IPv6 address
    */
   public static boolean isValidInet6Address(String inet6Address)
   {
      try
      {
         return (InetAddress.getByName(inet6Address) instanceof Inet6Address) && (inet6Address.indexOf(":") > -1);
      }
      catch (final UnknownHostException ex)
      {
         return false;
      }
   }

   /**
    * Compress an IPv6 address. Return compressed address, e.g. 0:0:0:0:0:0:0:01 => ::1
    *
    * @param inet6Address the IPv6 address to compress
    * @return compressed IPv6 address
    * @throws UnknownHostException if host with IPv6 address is not alive
    */
   public static String getCompressedInet6Address(String inet6Address) throws UnknownHostException
   {
      String longAddress = Inet6Address.getByName(inet6Address).getHostAddress();
      return longAddress.replaceFirst("(^|:)(0+(:|$)){2,8}", "::");
   }

   /**
    * Get an IPv6 address with long format. Return IPv6 address without ::, e.g. ::1 => 0:0:0:0:0:0:0:1
    *
    * @param inet6Address the IPv6 address to format
    * @return long format IPv6 address
    * @throws UnknownHostException if host with IPv6 address is not alive
    */
   public static String getLongInet6Address(String inet6Address) throws UnknownHostException
   {
      String longAddress = Inet6Address.getByName(inet6Address).getHostAddress();
      return longAddress;
   }
}
