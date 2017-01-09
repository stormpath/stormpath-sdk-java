#if( $springboot or $sczuul )
.. _change.html:

/templates/stormpath/change-password.html
=========================================

.. literalinclude:: ../../../../../extensions/spring/boot/stormpath-thymeleaf-spring-boot-starter/src/main/resources/templates/stormpath/change-password.html
   :language: html
   :linenos:
#elseif( $spring or $servlet )
.. _change.jsp:

/WEB-INF/jsp/stormpath/change-password.jsp
==========================================

.. literalinclude:: ../../../../../extensions/servlet/src/main/resources/META-INF/resources/WEB-INF/jsp/stormpath/change-password.jsp
   :language: none
   :linenos:
#end