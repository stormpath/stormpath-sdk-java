#if( $springboot or $sczuul )
.. _verify.html:

/templates/stormpath/verify.html
================================

.. literalinclude:: ../../../../../extensions/spring/boot/stormpath-thymeleaf-spring-boot-starter/src/main/resources/templates/stormpath/verify.html
   :language: html
   :linenos:
#elseif( $spring or $servlet )
.. _verify.jsp:

/WEB-INF/jsp/stormpath/verify.jsp
=================================

.. literalinclude:: ../../../../../extensions/servlet/src/main/resources/META-INF/resources/WEB-INF/jsp/stormpath/verify.jsp
   :language: none
   :linenos:
#end