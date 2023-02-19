package org.apache.dubbo.samples.provider.protocol;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.UrlUtils;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.RegistryFactory;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.model.ScopeModelUtil;

import java.util.ArrayList;
import java.util.List;

import static org.apache.dubbo.common.constants.RegistryConstants.REGISTRY_KEY;
import static org.apache.dubbo.common.constants.RegistryConstants.REGISTRY_PROTOCOL;
import static org.apache.dubbo.registry.Constants.DEFAULT_REGISTRY;

/**
 * @author 86187
 * @description <TODO description class purpose>
 * @create 2023/2/19 19:46
 *
 *  控制服务的上下线
 **/
public class OverrideProtocolWrapper implements Protocol {
    private Protocol protocol;

    private List<URL> UN_REGISTRY_URL_LIST = new ArrayList<>();

    @Override
    public int getDefaultPort() {
        return protocol.getDefaultPort();
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        if(UrlUtils.isRegistry(invoker.getUrl())){
            final Registry registry = getRegistry(invoker);
            doRegistryOverrideUrl(invoker,registry);
        }

        return null;
    }

    private <T> void doRegistryOverrideUrl(Invoker<T> invoker,Registry registry) {
        URL originalProviderUrl = getProviderUrl(invoker);
        UN_REGISTRY_URL_LIST.add(originalProviderUrl);
        OverrideBean override = new OverrideBean();

        override.setAddress(originalProviderUrl.getAddress());
        override.setService(originalProviderUrl.getServiceKey());
        override.setEnabled(true);
        override.setParams("disabled=true");
        // 将禁用协议写到注册中心去 registry.register(override.toUrl());
        registry.register(override.toUrl());
    }

    private <T> Registry getRegistry(Invoker<T> invoker) {
        URL registryUrl = invoker.getUrl();
        if((REGISTRY_PROTOCOL.equals(registryUrl.getProtocol()))){
            String protocol = registryUrl.getParameter(REGISTRY_KEY,DEFAULT_REGISTRY);
            registryUrl = registryUrl.setProtocol(protocol).removeParameter(REGISTRY_KEY);
        }
        RegistryFactory registryFactory = ScopeModelUtil.
                getExtensionLoader(RegistryFactory.class, registryUrl.getScopeModel()).getAdaptiveExtension();
        return registryFactory.getRegistry(registryUrl);
    }


    private URL getProviderUrl(final Invoker originInvoker) {
        return (URL) originInvoker.getUrl().getAttribute("export");
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        return null;
    }

    @Override
    public void destroy() {

    }
}
