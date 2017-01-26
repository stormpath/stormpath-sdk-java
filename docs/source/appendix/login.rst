#if( $springboot or $sczuul )
.. _login.html:

/templates/stormpath/login.html
===============================

.. literalinclude:: ../../../../../extensions/spring/boot/stormpath-thymeleaf-spring-boot-starter/src/main/resources/templates/stormpath/login.html
   :language: html
   :linenos:
#elseif( $spring or $servlet )
.. _login.jsp:

/WEB-INF/jsp/stormpath/login.jsp
================================

.. literalinclude:: ../../../../../extensions/servlet/src/main/resources/META-INF/resources/WEB-INF/jsp/stormpath/login.jsp
   :language: none
   :linenos:
#end
