.. _appendix:

Appendix
========

.. toctree::
   :maxdepth: 1

#if($servlet)
   appendix/web-stormpath-properties
#else
   appendix/default-stormpath-properties
#end
   appendix/i18n-properties
   appendix/stormpath-css
#if(!$servlet)
   appendix/change-password
   appendix/forgot-password
   appendix/head
   appendix/login
   appendix/register
   appendix/verify
#end
