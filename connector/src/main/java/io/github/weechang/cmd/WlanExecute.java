package io.github.weechang.cmd;

import io.github.weechang.Connector;
import io.github.weechang.config.Command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * wlan 命令行执行器
 */
public class WlanExecute {

    /**
     * 校验WLAN配置文件是否正确
     * <p>
     * 校验步骤为：
     * ---step1 添加配置文件
     * ---step3 连接wifi
     * ---step3 ping校验
     */
    public synchronized boolean check(String ssid, String password) {
        System.out.println("check : " + password);
        try {
            String profileName = password + ".xml";
            if (addProfile(profileName)) {
                if (connect(ssid)) {
                    Thread.sleep(2000);
                    if (ping()) {
                        return true;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 列出所有信号较好的ssid
     *
     * @return 所有ssid
     */
    public static List<Ssid> listSsid() {

        List<Ssid> ssidList = new ArrayList<Ssid>();
        try {
            String cmd = Command.SHOW_NETWORKS;
            List<String> line = execute(cmd, null);
            int status = 0;
            if (line != null && line.size() > 0) {
                for (int i = 0; i < line.size(); i++) {
                    int ssid1 = line.get(i).indexOf("SSID");
                    if (ssid1 != -1) {
                        if (line.get(i).length() < 25) {
                            Ssid ssid = new Ssid();
                            status = i;
                            int lowerTable = line.get(i).indexOf(":");
                            if (lowerTable != -1) {
                                String trim = line.get(i).substring(lowerTable + 1).trim();
                                if ("".equals(trim)) {
                                    continue;
                                }
                                ssid.setName(trim);
                            }
                            int mode = line.get(status + 2).indexOf("身份验证");
                            if (mode != -1) {
                                int index = line.get(status + 2).indexOf(":");
                                if (index != -1) {
                                    String substring1 = line.get(status + 2).substring(index + 1);
                                    ssid.setAuth(substring1.trim());
                                }
                            }
                            int signal = line.get(status + 5).indexOf("信号");
                            if (signal != -1) {
                                String substring = line.get(status + 5).substring(mode + 1);
                                int index = substring.indexOf(":");
                                if (index != -1) {
                                    String substring1 = substring.substring(index + 1);
                                    ssid.setdB(Integer.valueOf(substring1.trim().replace("%", "")));
                                }
                            }
                            if (ssid.getAuth() == null && ssid.getAuth() == "" && ssid.getdB() <= 0) {
                                ssid = null;
                            } else {
                                ssidList.add(ssid);
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssidList;
    }

    /**
     * 添加配置文件
     *
     * @param profileName 添加配置文件
     */
    private static boolean addProfile(String profileName) {
        String cmd = Command.ADD_PROFILE.replace("FILE_NAME", profileName);
        List<String> result = execute(cmd, Connector.PROFILE_TEMP_PATH);
        if (result != null && result.size() > 0) {
            if (result.get(0).contains("添加到接口")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 连接wifi
     *
     * @param ssid 添加配置文件
     */
    private static boolean connect(String ssid) {
        boolean connected = false;
        String cmd = Command.CONNECT.replace("SSID_NAME", ssid);
        List<String> result = execute(cmd, null);
        if (result != null && result.size() > 0) {
            if (result.get(0).contains("已成功完成")) {
                connected = true;
            }
        }
        return connected;
    }

    /**
     * ping 校验
     */
    private static boolean ping() {
        boolean pinged = false;
        String cmd = "ping " + Connector.PING_DOMAIN;
        List<String> result = execute(cmd, null);
        if (result != null && result.size() > 0) {
            for (String item : result) {
                if (item.contains("来自")) {
                    pinged = true;
                    break;
                }
            }
        }
        return pinged;
    }

    /**
     * 执行器
     *
     * @param cmd      CMD命令
     * @param filePath 需要在哪个目录下执行
     */
    private static List<String> execute(String cmd, String filePath) {
        Process process = null;
        List<String> result = new ArrayList<String>();
        try {
            if (filePath != null) {
                process = Runtime.getRuntime().exec(cmd, null, new File(filePath));
            } else {
                process = Runtime.getRuntime().exec(cmd);
            }
            BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "gbk"));
            String line = null;
            while ((line = bReader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}