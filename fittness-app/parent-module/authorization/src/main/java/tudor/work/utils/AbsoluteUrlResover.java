package tudor.work.utils;

import javax.servlet.http.HttpServletRequest;

public class AbsoluteUrlResover {
    public static String getSIteUrl(HttpServletRequest request)
    {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(),"");
    }
}
