package tudor.work.service;

import com.google.common.net.HttpHeaders;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import tudor.work.config.PayPalConfig;
import tudor.work.paypal.PayPalHttpClient;
import tudor.work.paypal.ValidateWebhookServlet;
import tudor.work.paypal.dtos.AccessTokenResponseDTO;
import tudor.work.utils.PayPalUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static tudor.work.paypal.PayPalEndpoints.GET_ACCESS_TOKEN;
import static tudor.work.paypal.PayPalEndpoints.createUrl;

@Service
@RequiredArgsConstructor
public class PayPalService {

    private final PayPalHttpClient payPalHttpClient;

    private final PayPalUtils payPalUtils;
    public AccessTokenResponseDTO getAccessToken() throws Exception {
        return payPalHttpClient.getAccessToken();
    }

    public Boolean webhookSignatureVerification(HttpServletRequest req) throws IOException {
        return payPalUtils.validateWebhookSignature(req);
    }


}
