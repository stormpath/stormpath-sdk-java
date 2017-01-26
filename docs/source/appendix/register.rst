#if( $springboot or $sczuul )
.. _register.html:

/templates/stormpath/register.html
==================================

.. literalinclude:: ../../../../../extensions/spring/boot/stormpath-thymeleaf-spring-boot-starter/src/main/resources/templates/stormpath/register.html
   :language: html
   :linenos:
#elseif( $spring or $servlet )
.. _register.jsp:

/WEB-INF/jsp/stormpath/register.jsp
===================================

.. literalinclude:: ../../../../../extensions/servlet/src/main/resources/META-INF/resources/WEB-INF/jsp/stormpath/register.jsp
   :language: none
   :linenos:
#end