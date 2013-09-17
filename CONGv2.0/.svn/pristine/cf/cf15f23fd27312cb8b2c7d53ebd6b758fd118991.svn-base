/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.networking;

import edu.ucsc.leeps.fire.FIREClientInterface;
import edu.ucsc.leeps.fire.client.ClientController;
import edu.ucsc.leeps.fire.client.ClientControllerInterface;
import edu.ucsc.leeps.fire.client.LoginDialog;
import edu.ucsc.leeps.fire.logging.Dialogs;
import edu.ucsc.leeps.fire.logging.Logger;
import edu.ucsc.leeps.fire.reflection.ClassFinder;
import edu.ucsc.leeps.fire.server.ServerController;
import edu.ucsc.leeps.fire.server.SessionControllerInterface;
import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;
import gnu.cajo.utils.extra.TransparentItemProxy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 *
 * @author jpettit
 */
public class NetworkUtil {

    public static void attachServerController(ServerController controller) {
        try {
            writeServerURL();
            Remote.config(getPreferredHostAddress(), 1198, getPreferredHostAddress(), 1198);
            ItemServer.bind(controller, "server");
        } catch (IOException ex) {
            Dialogs.popUpAndExit(ex);
        }
    }

    public static void connectToServer(ClientController controller) {
        SessionControllerInterface server;
        ClientControllerInterface controllerProxy;
        try {
            server =
                    (SessionControllerInterface) TransparentItemProxy.getItem(
                    discoverServerURL(),
                    new Class[]{SessionControllerInterface.class});
        } catch (Exception ex) {
            Dialogs.popUpAndExit("Could not connect to server.");
            return;
        }

        try {
            Remote.config(getPreferredHostAddress(), 0, getPreferredHostAddress(), 0);
            controllerProxy =
                    (ClientControllerInterface) TransparentItemProxy.getItem(
                    new Remote(controller),
                    new Class[]{ClientControllerInterface.class});
        } catch (RemoteException ex) {
            Dialogs.popUpAndExit(ex);
            return;
        }

        String name;
        if (System.getProperty("fire.id") != null) {
            name = System.getProperty("fire.id");
        } else {
            LoginDialog loginDialog = new LoginDialog();
            name = loginDialog.getUsername();
        }
        if (name.equals("")) {
            System.exit(0);
        }
        controller.setName(name);

        try {
            FIREClientInterface client = (FIREClientInterface) TransparentItemProxy.getItem(new Remote(ClassFinder.newClient()),
                    new Class[]{FIREClientInterface.class, ClassFinder.getClientInterfaceClass()});
            controller.setClient(client);
        } catch (RemoteException ex) {
            Dialogs.popUpAndExit(ex);
        }
        controller.setServer(server.getServerInterface());

        server.register(controllerProxy, name);
    }

    private static void writeServerURL() throws IOException {
        BufferedWriter ipFileWriter = new BufferedWriter(new FileWriter(Logger.getCurrentWorkingDir() + "ip_address.txt"));
        ipFileWriter.write(getPreferredHostAddress() + "\n");
        ipFileWriter.close();
    }

    private static String discoverServerURL() {
        try {
            BufferedReader ipFileReader = new BufferedReader(new FileReader(Logger.getCurrentWorkingDir() + "ip_address.txt"));
            String ip = ipFileReader.readLine();
            return "//" + ip + ":1198/" + "server";
        } catch (Exception ex) {
            Dialogs.popUpAndExit(ex);
        }
        return "";
    }

    private static String getPreferredHostAddress() {
        try {
            String globalAddr = null;
            String subnetAddr = null;
            String localAddr = null;
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.getHostAddress().contains(":")) {
                        if (addr.getHostAddress().startsWith("192")) {
                            subnetAddr = addr.getHostAddress();
                        } else if (addr.getHostAddress().startsWith("127") || addr.getHostAddress().startsWith("10")) {
                            localAddr = addr.getHostAddress();
                        } else {
                            globalAddr = addr.getHostAddress();
                        }
                    }
                }
            }
            String host = null;
            if (globalAddr != null) {
                host = globalAddr;
            } else if (subnetAddr != null) {
                host = subnetAddr;
            } else {
                host = localAddr;
            }
            return host;
        } catch (SocketException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
