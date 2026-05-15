package orip.stocks_prediction_system.utilities;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.theme.lumo.Lumo;

public class UtilsHelper
{
   public static void showNotification(String text, int duration, Position position, NotificationVariant... variants)
   {
      Notification notification = new Notification(text, duration, position);
      notification.addThemeVariants(variants);
      notification.open();
   }

   // ================== Dark/Light Mode ================
   public static void setThemeDarkMode(boolean darkMode)
   {
      UI.getCurrent().getElement().setAttribute("theme", darkMode ? Lumo.DARK : Lumo.LIGHT);
   }

   public static void setComponentDarkMode(Component component, boolean darkMode)
   {
      if (darkMode)
      {
         component.getElement().getThemeList().remove(Lumo.LIGHT);
         component.getElement().getThemeList().add(Lumo.DARK);
      }
      else
      {
         component.getElement().getThemeList().remove(Lumo.DARK);
         component.getElement().getThemeList().add(Lumo.LIGHT);
      }
   }
}
