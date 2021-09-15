package com.threadpool.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * ip相关工具类
 *
 * @author cyy
 * @date 2021/03/15 10:59
 **/
public class IpUtils {
    /**
     * 获取本机ip
     * @author cyy
     * @date 2021/04/09 20:27
     * @return java.lang.String
     */
    public static String getHostIp() {
        String ipStr = "";
        InetAddress ip = null;
        try {
            boolean findIp = false;
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                if (findIp) {
                    break;
                }
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    ip = ips.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                        findIp = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != ip) {
            ipStr = ip.getHostAddress();
        }
        return ipStr;
    }
}
