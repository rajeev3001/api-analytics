package org.wso2.carbon.api.analytics.alerts.core.internal;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.wso2.carbon.event.builder.stub.EventBuilderAdminServiceStub;
import org.wso2.carbon.event.formatter.stub.EventFormatterAdminServiceStub;
import org.wso2.carbon.event.input.adaptor.manager.stub.InputEventAdaptorManagerAdminServiceStub;
import org.wso2.carbon.event.output.adaptor.manager.stub.OutputEventAdaptorManagerAdminServiceStub;
import org.wso2.carbon.event.processor.stub.EventProcessorAdminServiceStub;
import org.wso2.carbon.ui.CarbonUIUtil;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;

public class AlertConfigurationUtil {


    public static EventProcessorAdminServiceStub getEventProcessorAdminService(
            ServletConfig config, HttpSession session)
            throws AxisFault {
//        ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
//                .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
        //Server URL which is defined in the server.xml

        ConfigurationContext configContext =
                ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);


        String serverURL = CarbonUIUtil.getServerURL(config.getServletContext(),
                session) + "EventProcessorAdminService.EventProcessorAdminServiceHttpsSoap12Endpoint";
        EventProcessorAdminServiceStub stub = new EventProcessorAdminServiceStub(configContext, serverURL);

        String cookie = (String) session.getAttribute(org.wso2.carbon.utils.ServerConstants.ADMIN_SERVICE_COOKIE);

        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);

        return stub;
    }

    public static EventFormatterAdminServiceStub getEventFormatterAdminService() throws AxisFault {
        return null;
    }

    public static EventBuilderAdminServiceStub getEventBuilderAdminService() throws AxisFault {
        return null;
    }

    public static InputEventAdaptorManagerAdminServiceStub getInputEventAdaptorAdminService() throws  AxisFault {
        return null;
    }

    public static OutputEventAdaptorManagerAdminServiceStub getOutputEVentAdaptorAdminService() throws AxisFault {
        return null;
    }

}
