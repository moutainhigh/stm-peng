package com.mainsteam.stm.plugin.fusioncompute.collect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.jdom2.JDOMException;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.plugin.fusioncompute.bo.ConnectionInfo;
import com.mainsteam.stm.plugin.fusioncompute.bo.FusionComputeResourceNode;
import com.mainsteam.stm.plugin.fusioncompute.dict.FCResourceID;
import com.mainsteam.stm.plugin.fusioncompute.utils.FusionComputeTools;
import com.huawei.esdk.fusioncompute.local.ServiceFactory;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.FCSDKResponse;
import com.huawei.esdk.fusioncompute.local.model.PageList;
import com.huawei.esdk.fusioncompute.local.model.cluster.ClusterBasicInfo;
import com.huawei.esdk.fusioncompute.local.model.cluster.ClusterInfo;
import com.huawei.esdk.fusioncompute.local.model.common.LoginResp;
import com.huawei.esdk.fusioncompute.local.model.common.Objectmetric;
import com.huawei.esdk.fusioncompute.local.model.common.QueryObjectmetricReq;
import com.huawei.esdk.fusioncompute.local.model.common.QueryObjectmetricResp;
import com.huawei.esdk.fusioncompute.local.model.host.HostBasicInfo;
import com.huawei.esdk.fusioncompute.local.model.host.HostInfo;
import com.huawei.esdk.fusioncompute.local.model.host.QueryHostListReq;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import com.huawei.esdk.fusioncompute.local.model.storage.Datastore;
import com.huawei.esdk.fusioncompute.local.model.storage.DatastoreQueryParams;
import com.huawei.esdk.fusioncompute.local.model.vm.Disk;
import com.huawei.esdk.fusioncompute.local.model.vm.Nic;
import com.huawei.esdk.fusioncompute.local.model.vm.QueryVmsReq;
import com.huawei.esdk.fusioncompute.local.model.vm.VmConfig;
import com.huawei.esdk.fusioncompute.local.model.vm.VmInfo;
import com.huawei.esdk.fusioncompute.local.resources.cluster.ClusterResource;
import com.huawei.esdk.fusioncompute.local.resources.common.AuthenticateResource;
import com.huawei.esdk.fusioncompute.local.resources.common.MonitorResource;
import com.huawei.esdk.fusioncompute.local.resources.host.HostResource;
import com.huawei.esdk.fusioncompute.local.resources.site.SiteResource;
import com.huawei.esdk.fusioncompute.local.resources.storage.DataStorageResource;
import com.huawei.esdk.fusioncompute.local.resources.vm.VmResource;

/**
 * Huawei virtualization acquisition plugin class ???????????????21??? getTree()
 * 
 * @author yuanlb
 * @2016???1???14??? ??????11:00:09
 */
public class FusionComputeCollector {

    private static final Logger debug = LogManager
            .getLogger(FusionComputeCollector.class);

    @SuppressWarnings("unused")
    private static final int TIME_WINDOW = 180;

    private String siteUri;

    String siteName;

    private static final String TRUE = "1";

    private static final String FALSE = "0";

    private static final String UNKNWON = "-1";

    @SuppressWarnings("unused")
    private ConnectionInfo connectionInfo;

    private ServiceFactory serviceFactory;

    private ClientProviderBean connection;

    private ClusterBasicInfo clusterInfo_element_1;

    @SuppressWarnings("unused")
    private HostBasicInfo hostBasicInfo_element_1;

    @SuppressWarnings("unused")
    private VmInfo vmInfos_element_1;

    private static String password;

    public FusionComputeTools fusionComputeTools = new FusionComputeTools();

    private SiteResource siteResourceService;
    
    private AuthenticateResource authenticateResourceService;
    public FusionComputeCollector() {
    }

    @SuppressWarnings({ "static-access", "unused" })
    public FusionComputeCollector(ConnectionInfo connectionInfo) {

        this.connectionInfo = connectionInfo;
        connection = new ClientProviderBean();
        connection.setServerIp(connectionInfo.serverIp);
        connection.setServerPort(connectionInfo.port);
        connection.setUserName(connectionInfo.username);
        password = connectionInfo.password;
        authenticateResourceService= serviceFactory
                .getService(AuthenticateResource.class, connection);

        FCSDKResponse<LoginResp> login = authenticateResourceService.login(
                connection.getUserName(), password);
        /*
         * if ("00000000".equals(login.getErrorCode())) {
         * System.out.println("FusionCompute????????????!!!????????????" +
         * connection.getUserName() + "???" + "????????????" + password + "???" + "IP??????" +
         * connection.getServerIp() + "???");
         * debug.error("FusionCompute????????????!!!????????????" + connection.getUserName() +
         * "???" + "????????????" + password + "???" + "IP??????" + connection.getServerIp() +
         * "???"); } else { System.out.println("FusionCompute????????????!!!????????????" +
         * connection.getUserName() + "???" + "????????????" + password + "???" + "IP??????" +
         * connection.getServerIp() + "???");
         * debug.error("FusionCompute????????????!!!????????????" + connection.getUserName() +
         * "???" + "????????????" + password + "???" + "IP??????" + connection.getServerIp() +
         * "???"); }
         */
    }

    /*
     * @Override public void run() { new Thread(new
     * FusionComputeCollector()).start(); new Thread(new
     * FusionComputeCollector()).start(); }
     */

    public static void main(String[] args) throws InstantiationException,
            IllegalAccessException, IOException, JDOMException, XmlRpcException {
        String ip = "172.19.39.207";
        String port = "7443";
        String username = "testuser1";
        password = "Huawei@12345";

        System.out .println(new FusionComputeCollector(new ConnectionInfo(ip,
        port, username, password)).getResourceTree());
        
        
        /*   System.out.println("CPU?????????");
        /service/sites/2F24053A/clusters/234 ??????esdk
         ??????01 /service/sites/2F24053A/datastores/1
        ??????JASON 1 /service/sites/2F24053A/clusters/988 ??????
        ??????eSDK  /service/sites/2F24053A/clusters/234 ??????
        */
      /* System.out.println("----------------??????JASON1 ?????????-------------------");

        System.out
                .println(new FusionComputeCollector(new ConnectionInfo(ip,
                        port, username, password))
                        .getClusterTotalCpuSize("/service/sites/2F24053A/clusters/988"));

        
          System.out.println("CPU?????????"); // /service/sites/2F24053A/clusters/234
         // ??????esdk // ??????01 /service/sites/2F24053A/datastores/1
         
          System.out.println("CPU????????????"); System.out .println(new
          FusionComputeCollector(new ConnectionInfo(ip, port, username,
          password))
          .getClusterObligateCpuSize("/service/sites/2F24053A/clusters/988"
         ));// ??????01
         
         
        System.out.println("CPU?????????????????????");
        System.out
                .println(new FusionComputeCollector(new ConnectionInfo(ip,
                        port, username, password))
                        .getClusterObligateCpuSizeRate("/service/sites/2F24053A/clusters/988"));

        
         System.out.println("CPU????????????"); System.out .println(new
          FusionComputeCollector(new ConnectionInfo(ip, port, username,
          password))
          .getClusterUsableCpuSize("/service/sites/2F24053A/clusters/988"));
         
        
         System.out .println(new FusionComputeCollector(new ConnectionInfo(ip,
          port, username, password)).getResourceTree());
         

        
         System.out.println("MEM?????????"); // /service/sites/2F24053A/clusters/234
         // ??????esdk // ??????01 /service/sites/2F24053A/datastores/1
          
         System.out .println(new FusionComputeCollector(new ConnectionInfo(ip,
          port, username, password))
          .getClusterTotalMemSize("/service/sites/2F24053A/clusters/988"));// ??????01
         
          System.out.println("MEM?????????"); // /service/sites/2F24053A/clusters/234  ??????esdk // ??????01 /service/sites/2F24053A/datastores/1
         
          System.out.println("MEM????????????"); System.out .println(new
          FusionComputeCollector(new ConnectionInfo(ip, port, username,
          password))
          .getClusterObligateMemSize("/service/sites/2F24053A/clusters/988"
          ));// ??????01
         System.out.println("MEM?????????????????????");
        System.out
                .println(new FusionComputeCollector(new ConnectionInfo(ip,
                        port, username, password))
                        .getClusterObligateMemSizeRate("/service/sites/2F24053A/clusters/988"));// ??????01

        
          System.out.println("MEM????????????"); System.out .println(new
          FusionComputeCollector(new ConnectionInfo(ip, port, username,
          password))
          .getClusterUsableMemSize("/service/sites/2F24053A/clusters/988"));// ??????01
          
          
          System.out.println("\n\n\n\n\n\"");
          
          System.out.println("----------------??????eSDK?????????-------------------");

          
          System.out
                  .println(new FusionComputeCollector(new ConnectionInfo(ip,
                          port, username, password))
                          .getClusterTotalCpuSize("/service/sites/2F24053A/clusters/234"));

          
            System.out.println("CPU?????????"); // /service/sites/2F24053A/clusters/234
           // ??????esdk // ??????01 /service/sites/2F24053A/datastores/1
           
            System.out.println("CPU????????????"); System.out .println(new
            FusionComputeCollector(new ConnectionInfo(ip, port, username,
            password))
            .getClusterObligateCpuSize("/service/sites/2F24053A/clusters/234"
           ));// ??????01
           
           
          System.out.println("CPU?????????????????????");
          System.out
                  .println(new FusionComputeCollector(new ConnectionInfo(ip,
                          port, username, password))
                          .getClusterObligateCpuSizeRate("/service/sites/2F24053A/clusters/234"));

          
           System.out.println("CPU????????????"); System.out .println(new
            FusionComputeCollector(new ConnectionInfo(ip, port, username,
            password))
            .getClusterUsableCpuSize("/service/sites/2F24053A/clusters/234"));
           
          
       
           

          
           System.out.println("MEM?????????"); // /service/sites/2F24053A/clusters/234
           // ??????esdk // ??????01 /service/sites/2F24053A/datastores/1
            
           System.out .println(new FusionComputeCollector(new ConnectionInfo(ip,
            port, username, password))
            .getClusterTotalMemSize("/service/sites/2F24053A/clusters/234"));// ??????01
           
            System.out.println("MEM?????????"); // /service/sites/2F24053A/clusters/234  ??????esdk // ??????01 /service/sites/2F24053A/datastores/1
           
            System.out.println("MEM????????????"); System.out .println(new
            FusionComputeCollector(new ConnectionInfo(ip, port, username,
            password))
            .getClusterObligateMemSize("/service/sites/2F24053A/clusters/234"
            ));// ??????01
           System.out.println("MEM?????????????????????");
          System.out
                  .println(new FusionComputeCollector(new ConnectionInfo(ip,
                          port, username, password))
                          .getClusterObligateMemSizeRate("/service/sites/2F24053A/clusters/234"));// ??????01

          
            System.out.println("MEM????????????"); System.out .println(new
            FusionComputeCollector(new ConnectionInfo(ip, port, username,
            password))
            .getClusterUsableMemSize("/service/sites/2F24053A/clusters/234"));// ??????01
*/    }

    public static String getTime(long time) {
        String str = "";
        time = time / 1000;
        int s = (int) (time % 60);
        int m = (int) (time / 60 % 60);
        int h = (int) (time / 3600);
        str = h + "??????" + m + "???" + s + "???";
        return str;
    }

    @SuppressWarnings("static-access")
    public String getResourceTree() {
        siteResourceService = serviceFactory.getService(SiteResource.class,
                connection);
        FCSDKResponse<List<SiteBasicInfo>> sites = siteResourceService
                .querySites();
        for (SiteBasicInfo siteBasicInfo_element : sites.getResult()) {
            siteUri = siteBasicInfo_element.getUri();
            siteName = siteBasicInfo_element.getName();
        }
        FusionComputeResourceNode root = new FusionComputeResourceNode(
                FCResourceID.FC_SITE, siteUri, null, siteName,
                FCResourceID.FC_SITE);
        discoveryFusionCompute(root);
        return JSON.toJSONString(root);
    }

//    private void discoveryFusionCompute(FusionComputeResourceNode currentNode) {
//
//        switch (currentNode.getResourceId()) {
//        case FCResourceID.FC_SITE:
//            @SuppressWarnings("static-access")
//            ClusterResource clusterResourceService = serviceFactory.getService(
//                    ClusterResource.class, connection);
//            FCSDKResponse<List<ClusterBasicInfo>> clusterBasicInfos = clusterResourceService
//                    .queryClusters(siteUri, null, null, null, null);
//        
//            for (ClusterBasicInfo clusterBasicInfo_element : clusterBasicInfos
//                    .getResult()) {
//                clusterInfo_element_1 = clusterBasicInfo_element;
//
//                FusionComputeResourceNode clusterNode = new FusionComputeResourceNode(
//
//                FCResourceID.FC_CLUSTER, clusterBasicInfo_element.getUri(),
//                        null, clusterBasicInfo_element.getName(),
//                        FCResourceID.FC_CLUSTER);
//                currentNode.getChildTrees().add(clusterNode);
//                discoveryFusionCompute(clusterNode);
//            }
//
//          
//            @SuppressWarnings("static-access")
//            DataStorageResource dataStorageResourceService = serviceFactory
//                    .getService(DataStorageResource.class, connection);
//            FCSDKResponse<PageList<Datastore>> datastoreInfos = dataStorageResourceService
//                    .queryDataStores(siteUri, new DatastoreQueryParams());
//            for (Datastore datastore_element : datastoreInfos.getResult()
//                    .getList()) {
//                FusionComputeResourceNode datastoreNode = new FusionComputeResourceNode(
//                        FCResourceID.FC_DATASTORAGE,
//                        datastore_element.getUri(), null,
//                        datastore_element.getName(),
//                        FCResourceID.FC_DATASTORAGE);
//                currentNode.getChildTrees().add(datastoreNode);
//                discoveryFusionCompute(datastoreNode);
//            }
//            break;
//       
//        case FCResourceID.FC_CLUSTER:
//            @SuppressWarnings({ "static-access" })
//            HostResource hostResourceService1 = serviceFactory.getService(
//                    HostResource.class, connection);
//            FCSDKResponse<PageList<HostBasicInfo>> hostBasicInfo1 = hostResourceService1
//                    .queryHostList(siteUri, new QueryHostListReq());
//           
//            for (HostBasicInfo hostBasicInfo_element : hostBasicInfo1
//                    .getResult().getList()) {
//                if (clusterInfo_element_1.getUrn().equals(
//                        hostBasicInfo_element.getClusterUrn())) {
//                    FusionComputeResourceNode hostNode = new FusionComputeResourceNode(
//                            FCResourceID.FC_HOST,
//                            hostBasicInfo_element.getUri(),
//                            hostBasicInfo_element.getIp(),
//                            hostBasicInfo_element.getName(),
//                            FCResourceID.FC_HOST);
//                    currentNode.getChildTrees().add(hostNode);
//                    discoveryFusionCompute(hostNode);
//                }
//            }
//
//      
//            @SuppressWarnings("static-access")
//            VmResource cluster_vmResource = serviceFactory.getService(
//                    VmResource.class, connection);
//            QueryVmsReq reqVm = new QueryVmsReq();
//            reqVm.setLimit(100);
//            reqVm.setOffset(0);
//            System.out.println("VM????????????100:\n");
//            FCSDKResponse<PageList<VmInfo>> cluster_vmInfo = cluster_vmResource
//                    .queryVMs(reqVm, siteUri);
//            cluster_vmInfo = cluster_vmResource.queryVMs(reqVm, siteUri);
//            Integer vmTotalNum = cluster_vmInfo.getResult().getTotal();
//
//            for (VmInfo cluster_vmInfo_element : cluster_vmInfo.getResult()
//                    .getList()) {
//              
//                VmConfig vmConfig = cluster_vmInfo_element.getVmConfig();
//                String getClusterVmIp = null;
//                if (null != vmConfig) {
//                    for (Nic nic : vmConfig.getNics()) {
//                        getClusterVmIp = nic.getIp();
//                 
//                    }
//                }
//
//                if (null != clusterInfo_element_1.getUrn()
//                        && null != cluster_vmInfo_element.getClusterUrn()) {
//
//                    if (clusterInfo_element_1.getUrn().equals(
//                            cluster_vmInfo_element.getClusterUrn())) {
//                        FusionComputeResourceNode vmNode = new FusionComputeResourceNode(
//                                FCResourceID.FC_VM,
//                                cluster_vmInfo_element.getUri(),
//                                getClusterVmIp,
//                                cluster_vmInfo_element.getName(),
//                                FCResourceID.FC_VM);
//                        currentNode.getChildTrees().add(vmNode);
//                        discoveryFusionCompute(vmNode);
//                    }
//                }
//            }
//
//            if (vmTotalNum > 100) {
//               
//                reqVm.setLimit(100);
//                reqVm.setOffset(100);
//
//                FCSDKResponse<PageList<VmInfo>> cluster_vmInfoMaxOffset = cluster_vmResource
//                        .queryVMs(reqVm, siteUri);
//                cluster_vmInfo = cluster_vmResource.queryVMs(reqVm, siteUri);
//
//                for (VmInfo cluster_vmInfo_element : cluster_vmInfoMaxOffset
//                        .getResult().getList()) {
//                  
//                    VmConfig vmConfig = cluster_vmInfo_element.getVmConfig();
//                    String getClusterVmIp = null;
//                    if (null != vmConfig) {
//                        for (Nic nic : vmConfig.getNics()) {
//                            getClusterVmIp = nic.getIp();
//                          
//                        }
//                    }
//
//                    if (null != clusterInfo_element_1.getUrn()
//                            && null != cluster_vmInfo_element.getClusterUrn()) {
//
//                        if (clusterInfo_element_1.getUrn().equals(
//                                cluster_vmInfo_element.getClusterUrn())) {
//                            FusionComputeResourceNode vmNode = new FusionComputeResourceNode(
//                                    FCResourceID.FC_VM,
//                                    cluster_vmInfo_element.getUri(),
//                                    getClusterVmIp,
//                                    cluster_vmInfo_element.getName(),
//                                    FCResourceID.FC_VM);
//                            currentNode.getChildTrees().add(vmNode);
//                            discoveryFusionCompute(vmNode);
//                        }
//                    }
//                }
//            }
//
//            break;
//
//        case FCResourceID.FC_DATASTORAGE:
//            break;
//        case FCResourceID.FC_VM:
//            break;
//        default:
//            break;
//        }
//    }

    private void discoveryFusionCompute(FusionComputeResourceNode currentNode) {

        switch (currentNode.getResourceId()) {
        case FCResourceID.FC_SITE:
            @SuppressWarnings("static-access")
            ClusterResource clusterResourceService = serviceFactory.getService(
                    ClusterResource.class, connection);
            FCSDKResponse<List<ClusterBasicInfo>> clusterBasicInfos = clusterResourceService
                    .queryClusters(siteUri, null, null, null, null);
        
            for (ClusterBasicInfo clusterBasicInfo_element : clusterBasicInfos
                    .getResult()) {
                clusterInfo_element_1 = clusterBasicInfo_element;

                FusionComputeResourceNode clusterNode = new FusionComputeResourceNode(

                FCResourceID.FC_CLUSTER, clusterBasicInfo_element.getUri(),
                        null, clusterBasicInfo_element.getName(),
                        FCResourceID.FC_CLUSTER);
                currentNode.getChildTrees().add(clusterNode);
                discoveryFusionCompute(clusterNode);
            }

          
            @SuppressWarnings("static-access")
            DataStorageResource dataStorageResourceService = serviceFactory
                    .getService(DataStorageResource.class, connection);
            FCSDKResponse<PageList<Datastore>> datastoreInfos = dataStorageResourceService
                    .queryDataStores(siteUri, new DatastoreQueryParams());
            for (Datastore datastore_element : datastoreInfos.getResult()
                    .getList()) {
                FusionComputeResourceNode datastoreNode = new FusionComputeResourceNode(
                        FCResourceID.FC_DATASTORAGE,
                        datastore_element.getUri(), null,
                        datastore_element.getName(),
                        FCResourceID.FC_DATASTORAGE);
                currentNode.getChildTrees().add(datastoreNode);
                discoveryFusionCompute(datastoreNode);
            }
            break;
       
        case FCResourceID.FC_CLUSTER:
            @SuppressWarnings({ "static-access" })
            HostResource hostResourceService1 = serviceFactory.getService(
                    HostResource.class, connection);
            FCSDKResponse<PageList<HostBasicInfo>> hostBasicInfo1 = hostResourceService1
                    .queryHostList(siteUri, new QueryHostListReq());
           
            for (HostBasicInfo hostBasicInfo_element : hostBasicInfo1
                    .getResult().getList()) {
                if (clusterInfo_element_1.getUrn().equals(
                        hostBasicInfo_element.getClusterUrn())) {
                    FusionComputeResourceNode hostNode = new FusionComputeResourceNode(
                            FCResourceID.FC_HOST,
                            hostBasicInfo_element.getUri(),
                            hostBasicInfo_element.getIp(),
                            hostBasicInfo_element.getName(),
                            FCResourceID.FC_HOST);
                    currentNode.getChildTrees().add(hostNode);
                    discoveryFusionCompute(hostNode);
                }
            }

//hihuawei      
            @SuppressWarnings("static-access")
            //?????????????????????
            VmResource cluster_vmResource = serviceFactory.getService(
                    VmResource.class, connection);
            QueryVmsReq reqVm = new QueryVmsReq();
//            List<VmInfo> list = new ArrayList<VmInfo>();
            boolean tag = false;
            int offset = 0;
            int limit = 100;
            Integer vmTotalNum = 1000;
            reqVm.setIsTemplate(false);
            reqVm.setLimit(limit);
            while (offset < vmTotalNum) {
            	reqVm.setOffset(offset);
                offset += limit;
                System.out.println("limit-------------->" + limit);
                System.out.println("offset-------------->" + offset);
                debug.error("limit-------------->" + limit);
                debug.error("offset-------------->" + offset);
            
            FCSDKResponse<PageList<VmInfo>> cluster_vmInfo = cluster_vmResource
                    .queryVMs(reqVm, siteUri);
            cluster_vmInfo = cluster_vmResource.queryVMs(reqVm, siteUri);
//            Integer vmTotalNum = cluster_vmInfo.getResult().getTotal();
            //todo
//            list.addAll(cluster_vmInfo.getResult().getList());
            vmTotalNum = cluster_vmInfo.getResult().getTotal();
            debug.error("vmTotalNum-------------->" + vmTotalNum);
            for (VmInfo cluster_vmInfo_element : cluster_vmInfo.getResult()
                    .getList()) {
              
                VmConfig vmConfig = cluster_vmInfo_element.getVmConfig();
                String getClusterVmIp = null;
                if (null != vmConfig) {
                    for (Nic nic : vmConfig.getNics()) {
                        getClusterVmIp = nic.getIp();
                 
                    }
                }

                if (null != clusterInfo_element_1.getUrn()
                        && null != cluster_vmInfo_element.getClusterUrn()) {

                    if (clusterInfo_element_1.getUrn().equals(
                            cluster_vmInfo_element.getClusterUrn())) {
                   
                    	FusionComputeResourceNode vmNode = new FusionComputeResourceNode(
                                FCResourceID.FC_VM,
                                cluster_vmInfo_element.getUri(),
                                getClusterVmIp,
                                cluster_vmInfo_element.getName(),
                                FCResourceID.FC_VM);
                        currentNode.getChildTrees().add(vmNode);
                        discoveryFusionCompute(vmNode);
                    }
                }
            }
          }
            break;
        case FCResourceID.FC_DATASTORAGE:
            break;
        case FCResourceID.FC_VM:
            break;
        default:
            break;
        }
    }
    
    /*
     * }; Thread thread = new Thread(runnable); thread.start(); }
     */

    /**
     * get uri
     * 
     * @param siteUrn
     * @return uri
     * @desc uri:unquiet
     */
    @SuppressWarnings("static-access")
    public String getSiteNameLabel() {
        SiteResource siteResourceService = serviceFactory.getService(
                SiteResource.class, connection);
        FCSDKResponse<List<SiteBasicInfo>> sites = siteResourceService
                .querySites();
        for (SiteBasicInfo siteBasicInfo_element : sites.getResult()) {
            return siteBasicInfo_element.getName();
        }
        return null;
    }

    @SuppressWarnings("static-access")
    public String getSiteUuid() {
        SiteResource siteResourceService = serviceFactory.getService(
                SiteResource.class, connection);
        FCSDKResponse<List<SiteBasicInfo>> sites = siteResourceService
                .querySites();
        for (SiteBasicInfo siteBasicInfo_element : sites.getResult()) {
            return siteBasicInfo_element.getUri();
        }
        return null;
    }

    // cluster
    @SuppressWarnings("static-access")
    public String getClusterUuid(String uuid) {
        // String xxx = "/service/sites/2F24053A";
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        /*
         * FCSDKResponse<List<ClusterBasicInfo>> clusters =
         * clusterResourceService .queryClusters(uuid.substring(0,23).toString()
         * , null, null, null, null); for (ClusterBasicInfo
         * clusterBasicInfo_element : clusters.getResult()) { return
         * clusterBasicInfo_element.getUri();
         */
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        return cluster.getResult().getUri();
    }

    @SuppressWarnings({ "static-access", "unused" })
    public String getClusterAvailability(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        // api?????????cluster?????????????????????
        if (uuid != null) {
            return TRUE;
        }
        return FALSE;
    }

    @SuppressWarnings("static-access")
    public String getClusterNameLabel(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        return cluster.getResult().getUrn();
    }

    @SuppressWarnings("static-access")
    public String getClusterName(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        return cluster.getResult().getName();
    }

    @SuppressWarnings("static-access")
    public String getClusterAddress(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        return cluster.getResult().getStatistics();
    }

    public String getClusterCpuRate(String uuid) {
        Map<String, String> perfMetrics = getUuidPerfMetrics(uuid);
        if (null != perfMetrics.get("mem_usage")
                && !"".equals(perfMetrics.get("cpu_usage")))
            return String.valueOf(Double.valueOf(perfMetrics.get("cpu_usage")));
        else
            return "";
    }

    public String getClusterMemRate(String uuid) {
        Map<String, String> perfMetrics = getUuidPerfMetrics(uuid);
        if (null != perfMetrics.get("mem_usage")
                && !"".equals(perfMetrics.get("mem_usage")))
            return String.valueOf(Double.valueOf(perfMetrics.get("mem_usage")));
        else
            return "";
    }

    /**
     * info:?????? cpu?????????
     * 
     * @param uuid
     * @return
     */
    @SuppressWarnings({ "static-access", "unused" })
    public String getClusterTotalCpuSize(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        if (null != clusterResourceService.queryComputeResource(uuid)
                .getResult()) {

            System.out.println("???CPU???"
                    + clusterResourceService.queryComputeResource(uuid)
                            .getResult().getTotalSizeMHz() / 1000 + "GHZ");
            /*
             * System.out.println("??????CPU???"+clusterResourceService.
             * queryComputeResource
             * (uuid).getResult().getAllocatedSizeMHz()/1000+"GHZ");
             * System.out.println("??????CPU??????-??????");
             * 
             * 
             * System.out.println("????????????"+clusterResourceService.queryComputeResource
             * (uuid).getResult().getTotalSizeMB()/1024+"GB");
             * 
             * System.out.println("??????mem"+clusterResourceService.
             * queryComputeResource
             * (uuid).getResult().getAllocatedSizeMB()/1024+"GB");
             * 
             * System.out.println("??????MEM:???-??????");
             */

            // (????????????/?????????)*100 = ???????????????
            int totalCpuSize = clusterResourceService
                    .queryComputeResource(uuid).getResult().getTotalSizeMHz() / 1000;
            return String.valueOf(totalCpuSize);
        }
        return "0.0";
    }

    /**
     * info:?????? ??????CPU??????
     * 
     * @param uuid
     * @return
     */
    @SuppressWarnings({ "static-access", "unused" })
    public String getClusterObligateCpuSize(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        if (null != clusterResourceService.queryComputeResource(uuid)
                .getResult()) {
            System.out.println("??????CPU???"
                    + clusterResourceService.queryComputeResource(uuid)
                            .getResult().getAllocatedSizeMHz() / 1000 + "GHZ");
            int obligateCpuSize = clusterResourceService
                    .queryComputeResource(uuid).getResult()
                    .getAllocatedSizeMHz() / 1000;
            return String.valueOf(obligateCpuSize);
        }
        return "0.0";
    }

    /**
     * info:?????? ??????CPU?????? ???
     * 
     * @param uuid
     * @return
     */
    @SuppressWarnings({ "static-access", "unused" })
    public String getClusterObligateCpuSizeRate(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        if (null != clusterResourceService.queryComputeResource(uuid)
                .getResult()) {
            System.out.println("??????CPU???"
                    + clusterResourceService.queryComputeResource(uuid)
                            .getResult().getAllocatedSizeMHz() / 1000 + "GHZ");
            int obligateCpuSize = clusterResourceService
                    .queryComputeResource(uuid).getResult()
                    .getAllocatedSizeMHz() / 1000;
            int totalCpuSize = clusterResourceService
                    .queryComputeResource(uuid).getResult().getTotalSizeMHz() / 1000;
            if (0 < obligateCpuSize && 0 < totalCpuSize) {
                double obligateCpuSizeRate = (double) obligateCpuSize
                        / (double) totalCpuSize;
                return String.valueOf(new FusionComputeTools()
                        .conver2DecimalString(obligateCpuSizeRate * 100));
            }
        }
        return "0.00";
    }

    /**
     * info:?????? ??????CPU??????
     * 
     * @param uuid
     * @return
     */
    @SuppressWarnings({ "static-access", "unused" })
    public String getClusterUsableCpuSize(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        if (null != clusterResourceService.queryComputeResource(uuid)
                .getResult()) {
            int totalCpuSize = clusterResourceService
                    .queryComputeResource(uuid).getResult().getTotalSizeMHz() / 1000;// ???
            int obligateCpuSize = clusterResourceService
                    .queryComputeResource(uuid).getResult()
                    .getAllocatedSizeMHz() / 1000;// ??????
            int usableCpuSize = totalCpuSize - obligateCpuSize;
            return String.valueOf(usableCpuSize);
        }
        return "0.0";
    }

    // ----

    /**
     * info:?????? MEM?????????
     * 
     * @param uuid
     * @return
     */
    @SuppressWarnings({ "static-access", "unused" })
    public String getClusterTotalMemSize(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        if (null != clusterResourceService.queryComputeResource(uuid)
                .getResult()) {
            System.out.println("????????????"
                    + clusterResourceService.queryComputeResource(uuid)
                            .getResult().getTotalSizeMB() / 1024 + "GB");
            int totalMemSize = clusterResourceService
                    .queryComputeResource(uuid).getResult().getTotalSizeMB() / 1024;
            return String.valueOf(totalMemSize);
        }
        return "0.0";
    }

    /**
     * info:?????? ??????MEM??????
     * 
     * @param uuid
     * @return
     */
    @SuppressWarnings({ "static-access", "unused" })
    public String getClusterObligateMemSize(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        if (null != clusterResourceService.queryComputeResource(uuid)
                .getResult()) {
            System.out.println("??????mem"
                    + clusterResourceService.queryComputeResource(uuid)
                            .getResult().getAllocatedSizeMB() / 1024 + "GB");
            int obligateMemSize = clusterResourceService
                    .queryComputeResource(uuid).getResult()
                    .getAllocatedSizeMB() / 1024;
            return String.valueOf(obligateMemSize);
        }
        return "0.0";
    }

    /**
     * info:?????? ??????MEM?????? ???
     * 
     * @param uuid
     * @return
     */
    @SuppressWarnings({ "static-access", "unused" })
    public String getClusterObligateMemSizeRate(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        if (null != clusterResourceService.queryComputeResource(uuid)
                .getResult()) {
            System.out.println("??????mem"
                    + clusterResourceService.queryComputeResource(uuid)
                            .getResult().getAllocatedSizeMB() / 1024 + "GB");
            int obligateMemSize = clusterResourceService
                    .queryComputeResource(uuid).getResult()
                    .getAllocatedSizeMB() / 1024;

            int totalMemSize = clusterResourceService
                    .queryComputeResource(uuid).getResult().getTotalSizeMB() / 1024;
            if (0 < obligateMemSize && 0 < totalMemSize) {
                double obligateMemSizeRate = ((double) obligateMemSize / (double) totalMemSize);
                return String.valueOf(new FusionComputeTools()
                        .conver2DecimalString(obligateMemSizeRate * 100));
            }
        }
        return "0.00";
    }

    /**
     * info:?????? ??????MEM??????
     * 
     * @param uuid
     * @return
     */
    @SuppressWarnings({ "static-access", "unused" })
    public String getClusterUsableMemSize(String uuid) {
        ClusterResource clusterResourceService = serviceFactory.getService(
                ClusterResource.class, connection);
        FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                .queryCluster(uuid);
        if (null != clusterResourceService.queryComputeResource(uuid)
                .getResult()) {
            int totalMemSize = clusterResourceService
                    .queryComputeResource(uuid).getResult().getTotalSizeMB() / 1024;// ???
            int obligateMemSize = clusterResourceService
                    .queryComputeResource(uuid).getResult()
                    .getAllocatedSizeMB() / 1024;// ??????
            int usableMemSize = totalMemSize - obligateMemSize;
            return String.valueOf(usableMemSize);
        }
        return "0.0";
    }

    // ----

    /**
     * @done ?????? ?????????
     * @param hostUri
     * @return
     */
    public String getHostAvailability(String uuid) {
        try {
            @SuppressWarnings("static-access")
            HostResource hostResourceService = serviceFactory.getService(
                    HostResource.class, connection);
            FCSDKResponse<HostInfo> host = hostResourceService.queryHost(uuid);
            String status = host.getResult().getStatus();
            if (null != status && "00000000".equals(host.getErrorCode())) {
                return TRUE;
            } else {
                return FALSE;
            }
        } catch (Exception e) {
            debug.error("?????????????????????!!!" + e.getMessage());
        }
        return UNKNWON;
    }

    @SuppressWarnings("static-access")
    public String getHostNameLabel(String uuid) {
        HostResource hostResourceService = serviceFactory.getService(
                HostResource.class, connection);
        FCSDKResponse<HostInfo> host = hostResourceService.queryHost(uuid);
        return host.getResult().getUrn();
    }

    @SuppressWarnings("static-access")
    public String getHostUuid(String uuid) {
        HostResource hostResourceService = serviceFactory.getService(
                HostResource.class, connection);
        FCSDKResponse<HostInfo> host = hostResourceService.queryHost(uuid);
        return host.getResult().getUri();
    }

    @SuppressWarnings("static-access")
    public String getHostName(String uuid) {
        HostResource hostResourceService = serviceFactory.getService(
                HostResource.class, connection);
        FCSDKResponse<HostInfo> host = hostResourceService.queryHost(uuid);
        return host.getResult().getName();
    }

    @SuppressWarnings("static-access")
    public String getHostAddress(String uuid) {
        HostResource hostResourceService = serviceFactory.getService(
                HostResource.class, connection);
        FCSDKResponse<HostInfo> host = hostResourceService.queryHost(uuid);
        return host.getResult().getIp();
    }

    /*
     * public String getHostSysUpTime(String uuid) {
     * 
     * HostResource hostResourceService = serviceFactory.getService(
     * HostResource.class, connection); FCSDKResponse<HostInfo> host =
     * hostResourceService.queryHost(hostUri); return "";
     * 
     * return null; }
     */

    public String getHostCpuRate(String uuid) {
        Map<String, String> perfMetrics = getUuidPerfMetrics(uuid);
        return String.valueOf(Double.valueOf(perfMetrics.get("cpu_usage")));
    }

    public String getHostMemRate(String uuid) {
        Map<String, String> perfMetrics = getUuidPerfMetrics(uuid);
        return String.valueOf(Double.valueOf(perfMetrics.get("mem_usage")));
    }

    /*
     * public String getHostThroughput(String hostUri) {
     * 
     * return "\r\n" + "    physicalCpuQuantity\r\n" + "\r\n" +
     * "    private java.lang.Integer physicalCpuQuantity\r\n" + "\r\n" +
     * "    ??????CPU?????? ????????? ?????????\r\n" + "????????";
     * 
     * return null; }
     */

    /**
     * ???????????????******************************?????????
     * 
     * @param vmtUri
     *            PVDRIVERSTATTUS starting??? ????????? notRunning??? ????????? running??? ????????????
     * @return vmName status ?????????????????? running??? ????????? stopped??? ????????? unknown??? ?????????
     *         hibernated??? ????????? creating??? ???????????????????????????????????????????????????????????? shutting-down??? ?????????
     *         migrating??? ????????? fault-resuming??? ??????????????? starting??? ????????? stopping??? ?????????
     *         hibernating??? ????????? pause??? ????????? recycling??? ????????????
     */
    @SuppressWarnings("static-access")
    public String getVMAvailability(String uuid) {
        /*
         * // if(uuid!=null && !"".equals(uuid)) try { String status =
         * serviceFactory .getService(VmResource.class,
         * connection).queryVM(uuid) .getResult().getPvDriverStatus();
         * System.out.println("status====================>" + status); if
         * ("running".equals(status)) { return TRUE;// ????????? } else if
         * ("stopped".equals(status)) { return FALSE;// ?????? } else { return
         * UNKNWON; } } catch (Exception e) { debug.error(e.getMessage() +
         * " uuid===>" + uuid); } return null;
         */

        try {
            if (null != uuid && !"".equals(uuid)) {
                String status = serviceFactory
                        .getService(VmResource.class, connection).queryVM(uuid)
                        .getResult().getPvDriverStatus();
                if ("running".equals(status)) {
                    return TRUE;
                } else {
                    return FALSE;
                }
            }
        } catch (Exception e) {
            debug.error("????????????????????????!!!" + e.getMessage());
        }
        return UNKNWON;
    }

    @SuppressWarnings("static-access")
    public String getVMNameLabel(String uuid) throws XmlRpcException {
        return serviceFactory.getService(VmResource.class, connection)
                .queryVM(uuid).getResult().getName();
    }

    @SuppressWarnings("static-access")
    public String getVMUuid(String uuid) {
        return serviceFactory.getService(VmResource.class, connection)
                .queryVM(uuid).getResult().getUri();
    }

    // ?????????

    /**
     * ?????????????????????
     */
    @SuppressWarnings("static-access")
    public String getVMPowerState(String uuid) {
        return serviceFactory.getService(VmResource.class, connection)
                .queryVM(uuid).getResult().getStatus();
    }

    public String getVMCpuRate(String uuid) {
        Map<String, String> perfMetrics = getUuidPerfMetrics(uuid);
        return String.valueOf(Double.valueOf(perfMetrics.get("cpu_usage")));
    }

    public String getVMMemRate(String uuid) {
        Map<String, String> perfMetrics = getUuidPerfMetrics(uuid);
        return String.valueOf(Double.valueOf(perfMetrics.get("mem_usage")));
    }

    /**
     * ??????VM???????????????IP??????
     * 
     * @param uuid
     * @return
     */
    @SuppressWarnings("static-access")
    public String getVMAddress(String uuid) {

        VmInfo vm = serviceFactory.getService(VmResource.class, connection)
                .queryVM(uuid).getResult();
        if (null != vm) {
            String hostUuid = new FusionComputeTools().getUrnChangeUri(vm
                    .getHostUrn());
            System.out.println("hostUuid=====>" + hostUuid);
            /*********** ????????????IP?????? **************/
            HostResource hostResourceService = serviceFactory.getService(
                    HostResource.class, connection);
            FCSDKResponse<HostInfo> host = hostResourceService
                    .queryHost(hostUuid);
            System.out.println(host.getResult().getIp());
            return host.getResult().getIp();
            /**************** VMIP?????? */
            /*
             * VmConfig vmConfig = vm.getVmConfig(); for (Nic nic :
             * vmConfig.getNics()) {
             * System.out.println("vmIp====>"+nic.getIp()); return nic.getIp();
             * }
             */
        }
        return null;
    }

    // vm??????????????????
    @SuppressWarnings("static-access")
    public String getHostDiskinfoByVM(String uuid) {
        VmInfo vm = serviceFactory.getService(VmResource.class, connection)
                .queryVM(uuid).getResult();
        // System.out.println("getname===============>" + vm.getName());
        VmConfig vmConfig = vm.getVmConfig();
        for (Disk disk : vmConfig.getDisks()) {
            String diskInfo = "?????????" + disk.getVolumeUuid() + ",???????????????"
                    + disk.getDatastoreName() + ",?????????" + disk.getQuantityGB()
                    + "GB";
            // System.out.println("diskInfo==================>" + diskInfo);
            // debug.error("jason say:diskInfo===>"+diskInfo);
            return diskInfo;
        }
        return null;
    }

    /**
     * vmNics?????????????????????????????????
     * 
     * @param vmUri
     * @return
     */
    public String getVMThroughput(String uuid) {
        /*
         * return serviceFactory.getService(VmResource.class, connection)
         * .queryVM(vmUri).getResult().getVmConfig().getNics().toString();
         */
        return null;
    }

    // ????????????????????????
    /*
     * status ???????????? scope???clusterUrn(??????)
     * ??????storageUnitUrn(????????????)?????????????????????????????????????????????NORMAL??????
     * ???ABNORMAL????????????CREATING????????????DELETING????????? scope???hostUrn (??????)
     * ????????????????????????????????????????????????????????????
     * ???CONNECTING????????????CONNECTED????????????DISCONNECTING????????????ABNORMAL?????????
     * 
     * 
     * 
     * status getDataStoreAvailability 1.?????????????????????NORMAL, ABNORMAL(??????), CREATING,
     * DELETING??? (NORMAL?????????????????????????????????????????????????????????????????????????????????????????????)
     * 2.????????????????????????????????????(?????????CONNECTING????????????CONNECTED????????????DISCONNECTING?????????ABNORMAL)???
     */
    @SuppressWarnings("static-access")
    public String getDataStoreAvailability(String uuid) {
        /*
         * DataStorageResource datastoreService = serviceFactory.getService(
         * DataStorageResource.class, connection); String dataStaus =
         * datastoreService.queryDataStore(uuid).getResult() .getStatus();
         * System.out.println("dataStaus============>" + dataStaus); if
         * ("NORMAL".equals(dataStaus)) { return TRUE; } else if
         * ("ABNORMAL".equals(dataStaus)) { return FALSE; } else { return
         * UNKNWON; }
         */
        try {
            if (null != uuid && !"".equals(uuid)) {
                DataStorageResource datastoreService = serviceFactory
                        .getService(DataStorageResource.class, connection);
                String dataStaus = datastoreService.queryDataStore(uuid)
                        .getResult().getStatus();
                if ("NORMAL".equals(dataStaus)) {
                    return TRUE;
                } else {
                    return FALSE;
                }
            }
        } catch (Exception e) {
            debug.error("?????????????????????!!!" + e.getMessage());
        }
        return UNKNWON;
    }

    @SuppressWarnings("static-access")
    public String getDataStoreNameLabel(String uuid) throws XmlRpcException {
        DataStorageResource datastoreService = serviceFactory.getService(
                DataStorageResource.class, connection);
        return datastoreService.queryDataStore(uuid).getResult().getName();
    }

    @SuppressWarnings("static-access")
    public String getDataStoreUuid(String uuid) throws XmlRpcException {
        DataStorageResource datastoreService = serviceFactory.getService(
                DataStorageResource.class, connection);
        return datastoreService.queryDataStore(uuid).getResult().getUri();
    }

    @SuppressWarnings("static-access")
    public String getDataStoreType(String uuid) throws XmlRpcException {
        DataStorageResource datastoreService = serviceFactory.getService(
                DataStorageResource.class, connection);
        return datastoreService.queryDataStore(uuid).getResult()
                .getStorageType();
    }

    // ??????

    public String getDataStoreShared(String uuid) throws XmlRpcException { /*
                                                                            * DataStorageResource
                                                                            * datastoreService
                                                                            * =
                                                                            * serviceFactory
                                                                            * .
                                                                            * getService
                                                                            * (
                                                                            * DataStorageResource
                                                                            * .
                                                                            * class
                                                                            * ,
                                                                            * connection
                                                                            * );
                                                                            * return
                                                                            * datastoreService
                                                                            * .
                                                                            * queryDataStore
                                                                            * (
                                                                            * dataStoreUri
                                                                            * ).
                                                                            * getResult
                                                                            * (
                                                                            * ).
                                                                            * getc
                                                                            * );
                                                                            */
        return null;
    }

    public String getDataStoreAddress(String uuid) throws XmlRpcException { /*
                                                                             * DataStorageResource
                                                                             * datastoreService
                                                                             * =
                                                                             * serviceFactory
                                                                             * .
                                                                             * getService
                                                                             * (
                                                                             * DataStorageResource
                                                                             * .
                                                                             * class
                                                                             * ,
                                                                             * connection
                                                                             * )
                                                                             * ;
                                                                             * return
                                                                             * datastoreService
                                                                             * .
                                                                             * queryDataStore
                                                                             * (
                                                                             * dataStoreUri
                                                                             * )
                                                                             * .
                                                                             * getResult
                                                                             * (
                                                                             * )
                                                                             * .
                                                                             * getUrn
                                                                             * (
                                                                             * )
                                                                             * ;
                                                                             */
        return null;
    }

    /**
     * ???????????????
     * 
     * @param uuid
     * @return
     * @throws XmlRpcException
     *             logic_disk_usage
     */
    @SuppressWarnings("static-access")
    public String getDataStorePhysicalRate(String uuid) throws XmlRpcException {
        DataStorageResource datastoreService = serviceFactory.getService(
                DataStorageResource.class, connection);
        FCSDKResponse<Datastore> datastore = datastoreService
                .queryDataStore(uuid);
        // ???????????????
        Double usageVolume = Double.valueOf(datastore.getResult()
                .getUsedSizeGB());
        // System.out.println(usageVolume);
        // ????????????
        Double freeVolume = Double.valueOf(datastore.getResult()
                .getFreeSizeGB());
        // System.out.println(freeVolume);
        // ?????????
        Double totalVolume = usageVolume + freeVolume;
        // System.out.println(totalVolume);

        String dataStorePhysicalRate = String
                .valueOf((usageVolume / totalVolume) * 100);
        // System.out.println(dataStorePhysicalRate);
        return dataStorePhysicalRate;
        /*
         * Map<String, String> perfMetrics = getUuidPerfMetrics(uuid); return
         * String.valueOf(Double.valueOf(perfMetrics .get("logic_disk_usage")));
         */
    }

    /**
     * ?????????
     * 
     * @param uuid
     * @return
     * @throws XmlRpcException
     */
    public String getDataStorePhysicalSize(String uuid) {
        /*
         * ??????????????????10T????????????10T???????????? try {
         * 
         * @SuppressWarnings("static-access") DataStorageResource
         * datastoreService = serviceFactory.getService(
         * DataStorageResource.class, connection); FCSDKResponse<Datastore>
         * datastore = datastoreService .queryDataStore(uuid); if (null !=
         * datastore.getResult()) { // ??????????????? Double usageVolume =
         * Double.valueOf(datastore.getResult() .getUsedSizeGB()); //
         * System.out.println(usageVolume); // ???????????? Double freeVolume =
         * Double.valueOf(datastore.getResult() .getFreeSizeGB()); //
         * System.out.println(freeVolume); // ????????? Double totalVolume =
         * usageVolume + freeVolume; // System.out.println(totalVolume); String
         * dataStorePhysicalSize = String.valueOf(totalVolume); return
         * dataStorePhysicalSize; } else { return ""; } } catch (Exception e) {
         * debug
         * .error("fusioncompute.datastore.datastorephysicalsize kpi is error :"
         * + e.getCause()); } return "";
         */

        @SuppressWarnings("static-access")
        DataStorageResource datastoreService = serviceFactory.getService(
                DataStorageResource.class, connection);
        FCSDKResponse<Datastore> datastore = datastoreService
                .queryDataStore(uuid);
        if (null != datastore.getResult()) {
            datastore.getResult().getActualCapacityGB();
            return String.valueOf(datastore.getResult().getActualCapacityGB());
        }
        // actualCapacityGB
        return "";
    }

    /**
     * ????????????
     * 
     * @param uuid
     * @return
     * @throws XmlRpcException
     */
    public String getDataStorePhysicalUtilisation(String uuid) {
        @SuppressWarnings("static-access")
        DataStorageResource datastoreService = serviceFactory.getService(
                DataStorageResource.class, connection);
        FCSDKResponse<Datastore> datastore = datastoreService
                .queryDataStore(uuid);
        // ???????????????
        Double usageVolume = Double.valueOf(datastore.getResult()
                .getUsedSizeGB());
        String dataStorePhysicalUtilisation = String.valueOf(usageVolume);
        return dataStorePhysicalUtilisation;
    }

    @SuppressWarnings({ "unused", "static-access" })
    public Map<String, String> getUuidPerfMetrics(String uuid) {
        // ????????????????????????
        Map<String, String> uuidPerfMetrics = new HashMap<String, String>();

        if (uuid != null && !"".equals(uuid) && uuid.contains("/clusters")) {

            // System.out.println("hostUuid======>" + uuid);

            // XEN : http://192.168.1.201/rrd_updates?start=1453870731&host=true
            // https://172.19.39.207:8443/OmsPortal/index.jsp#computing/host-290

            // ????????????:
            // ??????????????????
            ClusterResource clusterResourceService = serviceFactory.getService(
                    ClusterResource.class, connection);
            FCSDKResponse<ClusterInfo> cluster = clusterResourceService
                    .queryCluster(uuid);
            System.out.println("?????????" + cluster.getResult().getName() + "\n");

            String siteUri = cluster
                    .getResult()
                    .getUri()
                    .substring(
                            0,
                            cluster.getResult().getUri().indexOf("clusters") - 1);
            // System.out.println("siteUri===========================>" +
            // siteUri);

            // ??????????????????
            MonitorResource monitorResourceService = ServiceFactory.getService(
                    MonitorResource.class, connection);

            // ????????????:
            // ??????????????????????????????????????????
            ArrayList<String> metricValues = new ArrayList<String>();
            metricValues.add("cpu_usage");
            metricValues.add("mem_usage");
            QueryObjectmetricReq req = new QueryObjectmetricReq();
            req.setUrn(cluster.getResult().getUrn());
            req.setMetricId(metricValues);
            List<QueryObjectmetricReq> listReq = new ArrayList<QueryObjectmetricReq>();
            listReq.add(req);
            // System.out.println(listReq);

            // ??????
            // KEY-VALUE
            Map<String, QueryObjectmetricReq> metricKeyValueMas = new HashMap<String, QueryObjectmetricReq>();
            for (QueryObjectmetricReq item_queryObjectmetricReq : listReq) {
                // System.out.println("Metirc???===> "+
                // item_queryObjectmetricReq.getMetricId());

                // ??????????????????????????????????????????
                // ??????
                // VALUE
                FCSDKResponse<QueryObjectmetricResp> resp = monitorResourceService
                        .queryObjectmetricRealtimedata(siteUri, listReq);

                for (Objectmetric item_objectmetric : resp.getResult()
                        .getItems()) {

                    for (int i = 0; i < item_objectmetric.getValue().size(); i++) {
                        // String metricObj = item_objectmetric.getObjectName();
                        String metricUrn = item_objectmetric.getUrn();
                        // System.out.println("metricUrn===>" + metricUrn);
                        String key = item_objectmetric.getValue().get(i)
                                .getMetricId();
                        String value = item_objectmetric.getValue().get(i)
                                .getMetricValue();
                        String metricValue = metricValues.get(i);
                        // System.out.println("metricValue===>" + metricValue);
                        System.out.println("key:" + key
                                + "________________________value:" + value);
                        uuidPerfMetrics.put(key, value);
                    }
                }
            }
        }

        if (uuid != null && !"".equals(uuid) && uuid.contains("/hosts")) {

            // System.out.println("hostUuid======>" + uuid);

            // XEN : http://192.168.1.201/rrd_updates?start=1453870731&host=true
            // https://172.19.39.207:8443/OmsPortal/index.jsp#computing/host-290

            HostResource hostResourceService = serviceFactory.getService(
                    HostResource.class, connection);
            FCSDKResponse<HostInfo> host = hostResourceService.queryHost(uuid);
            System.out.println("????????????" + host.getResult().getName() + "\n");

            // site
            String siteUri = host.getResult().getUri()
                    .substring(0, host.getResult().getUri().length() - 10)
                    .toString();
            // System.out.println("siteUri===========================>" +
            // siteUri);

   
            MonitorResource monitorResourceService = ServiceFactory.getService(
                    MonitorResource.class, connection);

            ArrayList<String> metricValues = new ArrayList<String>();
            metricValues.add("cpu_usage");
            metricValues.add("mem_usage");
            QueryObjectmetricReq req = new QueryObjectmetricReq();
            req.setUrn(host.getResult().getUrn());
            req.setMetricId(metricValues);
            List<QueryObjectmetricReq> listReq = new ArrayList<QueryObjectmetricReq>();
            listReq.add(req);
            // System.out.println(listReq);

        
            Map<String, QueryObjectmetricReq> metricKeyValueMas = new HashMap<String, QueryObjectmetricReq>();
            for (QueryObjectmetricReq item_queryObjectmetricReq : listReq) {
                // System.out.println("Metirc???===> "+
                // item_queryObjectmetricReq.getMetricId());

             
                FCSDKResponse<QueryObjectmetricResp> resp = monitorResourceService
                        .queryObjectmetricRealtimedata(siteUri, listReq);

                for (Objectmetric item_objectmetric : resp.getResult()
                        .getItems()) {

                    for (int i = 0; i < item_objectmetric.getValue().size(); i++) {
                        // String metricObj = item_objectmetric.getObjectName();
                        String metricUrn = item_objectmetric.getUrn();
                        // System.out.println("metricUrn===>" + metricUrn);
                        String key = item_objectmetric.getValue().get(i)
                                .getMetricId();
                        String value = item_objectmetric.getValue().get(i)
                                .getMetricValue();
                        String metricValue = metricValues.get(i);
                        // System.out.println("metricValue===>" + metricValue);
                        uuidPerfMetrics.put(key, value);
                    }
                }
            }
        }

        if (uuid != null && !"".equals(uuid) && uuid.contains("/vms")) {
            
            VmResource vmResourceServic = serviceFactory.getService(
                    VmResource.class, connection);
            FCSDKResponse<VmInfo> vm = vmResourceServic.queryVM(uuid);
            // System.out.println("????????????" + vm.getResult().getName() + "\n");
            // site
            String siteUri = vm.getResult().getUri()
                    .substring(0, vm.getResult().getUri().length() - 15)
                    .toString();
            // System.out.println("siteUri===========================>" +
            // siteUri);

            MonitorResource monitorResourceService = ServiceFactory.getService(
                    MonitorResource.class, connection);

            ArrayList<String> metricValues = new ArrayList<String>();

            metricValues.add("cpu_usage");
            metricValues.add("mem_usage");
            QueryObjectmetricReq req = new QueryObjectmetricReq();
            req.setUrn(vm.getResult().getUrn());
            req.setMetricId(metricValues);
            List<QueryObjectmetricReq> listReq = new ArrayList<QueryObjectmetricReq>();
            listReq.add(req);
           
            Map<String, QueryObjectmetricReq> metricKeyValueMas = new HashMap<String, QueryObjectmetricReq>();
            for (QueryObjectmetricReq item_queryObjectmetricReq : listReq) {
                // System.out.println("Metirc???===> "+
                // item_queryObjectmetricReq.getMetricId());

                FCSDKResponse<QueryObjectmetricResp> resp = monitorResourceService
                        .queryObjectmetricRealtimedata(siteUri, listReq);

                for (Objectmetric item_objectmetric : resp.getResult()
                        .getItems()) {

                    for (int i = 0; i < item_objectmetric.getValue().size(); i++) {
                        // String metricObj = item_objectmetric.getObjectName();
                        String metricUrn = item_objectmetric.getUrn();
                        // System.out.println("metricUrn===>" + metricUrn);
                        String key = item_objectmetric.getValue().get(i)
                                .getMetricId();
                        String value = item_objectmetric.getValue().get(i)
                                .getMetricValue();
                        String metricValue = metricValues.get(i);
                        // System.out.println("metricValue===>" + metricValue);
                        uuidPerfMetrics.put(key, value);
                    }
                }
            }
        }
        // System.out.println(uuidPerfMetrics);
        return uuidPerfMetrics;
    }

    public void dispose() {
        try {
            // AuthenticateResource.this.logout();
//        	connection.
        	authenticateResourceService.logout();
        } catch (Exception e) {
        }
    }
}
