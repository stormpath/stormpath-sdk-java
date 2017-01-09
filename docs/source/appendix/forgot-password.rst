#if( $springboot or $sczuul )
.. _forgot.html:

/templates/stormpath/forgot-password.html
=========================================

.. literalinclude:: ../../../../../extensions/spring/boot/stormpath-thymeleaf-spring-boot-starter/src/main/resources/templates/stormpath/forgot-password.html
   :language: html
   :linenos:
#elseif( $spring or $servlet )
.. _forgot.jsp:

/WEB-INF/jsp/stormpath/forgot-password.jsp
==========================================

.. literalinclude:: ../../../../../extensions/servlet/src/main/resources/META-INF/resources/WEB-INF/jsp/stormpath/forgot-password.jsp
   :language: none
   :linenos:
#end