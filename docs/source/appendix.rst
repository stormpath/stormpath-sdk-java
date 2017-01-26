.. _appendix:

Appendix
========

.. toctree::
   :maxdepth: 1

#if( $servlet )
   appendix/web-stormpath-properties
#else
   appendix/default-stormpath-properties
#end
   appendix/i18n-properties
   appendix/stormpath-css
   appendix/change-password
   appendix/forgot-password
#if( $springboot or $sczuul )
   appendix/head
#end
   appendix/login
   appendix/register
   appendix/verify
