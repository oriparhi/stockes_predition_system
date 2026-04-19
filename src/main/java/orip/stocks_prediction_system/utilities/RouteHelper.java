package orip.stocks_prediction_system.utilities;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteConfiguration;

/**
 * @author ILAN PERETZ | 23.12.2025
 */
public class RouteHelper
{
   public static <T extends Component> void navigateTo(Class<T> page)
   {
      String pageRoute = RouteConfiguration.forSessionScope().getUrl(page);
      if (pageRoute.isEmpty())
         pageRoute = "/";
      UI.getCurrent().getPage().setLocation(pageRoute);
   }

   public static void navigateTo(String pageRoute)
   {
      UI.getCurrent().getPage().setLocation(pageRoute);
   }
}
