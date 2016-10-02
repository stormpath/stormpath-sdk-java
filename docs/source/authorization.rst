.. only:: springboot

  .. _authorization:

  Authorization
  ==============

  If you are using our `Spring Security integration <https://github.com/stormpath/stormpath-sdk-java/tree/master/extensions/spring/stormpath-spring-security-webmvc>`_ you will obtain a seamless integration with Spring Security's authorization model. Besides the principal's information, Stormpath also populates `Spring Security's Granted Authorities <http://docs.spring.io/spring-security/site/docs/4.1.2.RELEASE/reference/html/technical-overview.html#tech-granted-authority>`_ for you. Once an authentication attempt is successful,
  you can perform standard Spring Security authority checks, like ``antMatchers("/**").hasAuthority("SOME_ROLE")`` or ``@PreAuthorize("hasAuthority('SOME_PERMISSION')")``

  Roles
  ~~~~~

  Spring Security's role concept in Stormpath is represented as a Stormpath `Group <http://docs.stormpath.com/java/product-guide/#groups>`__.

  Assigning Roles
  ^^^^^^^^^^^^^^^

  Spring Security's ``Authority`` concept is represented as a Stormpath ``Group``. During the authentication process all the groups belonging to an account are automatically populated as authorities in the authentication token. Therefore you assign a role to an account simply by adding an account to a group (or by adding a group to an account, depending on how you look at it). For example, for an account belonging to a specific group like this:

  .. code:: java

      String groupHref = "https://api.stormpath.com/v1/groups/d2UDkz1EPcn2a71j93m6D";
      Group group = client.getResource(groupHref, Group.class);

      //assign the account to the group:
      account.addGroup(group);

  When the account is logged in, the following authorization requirement will be met and the post operation will be allowed to be executed:

  .. code:: java

      @PreAuthorize("hasAuthority('https://api.stormpath.com/v1/groups/d2UDkz1EPcn2a71j93m6D')")
      public Account post(Account account, double amount) {
          //do something
      }


  Checking Roles
  ^^^^^^^^^^^^^^

  Role checks with the Group ``href``
  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  The recommended way to perform a Spring Security role check is to use the Stormpath group's ``href`` property as the Spring Security role 'name'.

  While it is possible (and maybe more intuitive) to use the Group name for the role check, this secondary approach is not enabled by default
  and not recommended for most usages: role names can potentially change over time (for example, someone changes the Group name in the Stormpath
  administration console without telling you). If you code a role check in your source code, and that role name changes in the future, your role
  checks will likely fail!

  Instead, it is recommended to perform role checks with a stable identifier.

  You can use a Stormpath Group's ``href`` property as the role 'name' and check that:

  .. code:: java

      @PreAuthorize("hasAuthority('A_SPECIFIC_GROUP_HREF')")
      public Account post(Account account, double amount) {
          //do something
      }

  Role checks with the Group ``name``
  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  If you still want to use a Stormpath Group's name as the Spring Security role name for role checks - perhaps because you have a high level of
  confidence that no one will change group names once your software is written - you can still use the Group name if you wish by adding a
  little configuration.

  In a ``@Configuration`` class, you can set the supported naming modes of what will be represented as a Spring Security role:

  .. code:: java

      @Bean
      public GroupGrantedAuthorityResolver defaultGroupGrantedAuthorityResolver() {
          DefaultGroupGrantedAuthorityResolver resolver = new DefaultGroupGrantedAuthorityResolver();
          Set<DefaultGroupGrantedAuthorityResolver.Mode> modes = new HashSet<>();
          modes.add(DefaultGroupGrantedAuthorityResolver.Mode.NAME);
          resolver.setModes(modes);
          return resolver;
      }

      @Bean
      @Autowired
      public StormpathAuthenticationProvider stormpathAuthenticationProvider(Application application) {
          StormpathAuthenticationProvider provider = new StormpathAuthenticationProvider(application);
          provider.setGroupGrantedAuthorityResolver(defaultGroupGrantedAuthorityResolver());
          return provider;
      }

  The modes (or mode names) allow you to specify which Group properties Spring Security will consider as role 'names'. The default is ``href``,
  but you can specify more than one if desired. The supported modes are the following:

  -  *HREF*: the Group's ``href`` property will be considered a Spring Security role name. This is the default mode if not configured
     otherwise. Allows a Spring Security role check to look like the following:
     ``authentication.getAuthorities().contains(new SimpleGrantedAuthority(group.getHref()))``.
  -  *NAME*: the Group's ``name`` property will be considered a Spring Security role name. This allows a Spring Security role check to look
     like the following:
     ``authentication.getAuthorities().contains(new SimpleGrantedAuthority(group.getName()))``.
     This however has the downside that if you (or someone else on your team or in your company) changes the Group's name, you will have to
     update your role check code to reflect the new names (otherwise the existing checks are very likely to fail).
  -  *ID*: the Group's unique id will be considered a Spring Security role name. The unique id is the id at the end of the Group's HREF url.
     This is a deprecated mode and should ideally not be used in new applications.

  The GroupGrantedAuthorityResolver Interface
  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  If the above default role name resolution logic does not meet your needs or if you want full customization of how a Stormpath Group resolves to
  one or more Spring Security role names, you can implement the ``GroupGrantedAuthorityResolver`` interface and configure the
  implementation on the StormpathAuthenticationProvider:

  .. code:: java

      @Bean
      public GroupGrantedAuthorityResolver myGroupGrantedAuthorityResolver() {
          MyGroupGrantedAuthorityResolver resolver = new MyGroupGrantedAuthorityResolver();
          ...
      }

      @Bean
      @Autowired
      public StormpathAuthenticationProvider stormpathAuthenticationProvider(Application application) {
          StormpathAuthenticationProvider provider = new StormpathAuthenticationProvider(application);
          provider.setGroupGrantedAuthorityResolver(myGroupGrantedAuthorityResolver());
          return provider;
      }

  Permissions
  ~~~~~~~~~~~

  The Spring Security plugin for Stormpath enables the ability to assign ad-hoc sets of permissions directly to Stormpath
  Accounts or Groups using the accounts' or groups' `Custom Data <https://docs.stormpath.com/rest/product-guide/latest/reference.html#custom-data>`__
  resource.

  Once assigned, the Stormpath ``AuthenticationProvider`` will automatically check account and group ``CustomData`` for permissions and
  create Spring Security Granted authorities that will be assigned to the authorization principal's authorities.

  Assigning Permissions
  ^^^^^^^^^^^^^^^^^^^^^

  The easiest way to assign permissions to an account or group is to get the account or group's ``CustomData`` resource and use the Spring
  Security Stormpath plugin's ``CustomDataPermissionsEditor`` to assign or remove permissions. The following example uses both the Stormpath Java
  SDK API and the Spring Security Stormpath plugin API:

  .. code:: java

      //Instantiate an account (this is the normal Stormpath Java SDK API):
      Account acct = client.instantiate(Account.class);
      String password = "Changeme1!";
      acct.setUsername("jsmith")
          .setPassword(password)
          .setEmail("jsmith@nowhere.com")
          .setGivenName("Joe")
          .setSurname("Smith");

      //Now let's add some Spring Security granted authorities to the account's customData:
      //(this class is in the Spring Security Stormpath Plugin API):
      new CustomDataPermissionsEditor(acct.getCustomData())
          .append("user:1234:edit")
          .append("report:create")

      //Add the new account with its custom data to an application (normal Stormpath Java SDK API):
      acct = anApplication.createAccount(Accounts.newCreateRequestFor(acct).build());

  You can assign permissions to a Group too:

  .. code:: java

      Group group = client.instantiate(Group.class);
      group.setName("Users");
      new CustomDataPermissionsEditor(group.getCustomData()).append("user:login");
      group = anApplication.createGroup(group)

  You might want to assign that account to the group. *Any permissions assigned to a group are automatically inherited by accounts in the
  group*:

  .. code:: java

      group.addAccount(acct);

  This is very convenient: You can assign permissions to many accounts simultaneously by simply adding them once to a group that the accounts
  share. In doing this, the Stormpath ``Group`` is acting much more like a role.

  Checking Permissions
  ^^^^^^^^^^^^^^^^^^^^

  So, in order to have Spring Security doing permissions check the way we intend, we need to create our own ``PermissionEvaluator``. The plugin
  provides ``WildcardPermissionEvaluator`` that is able to compare ``WildcardPermission``\s. In order to use it you need to configure Spring this way:

  .. code:: java

      import com.stormpath.spring.security.authz.permission.evaluator.WildcardPermissionEvaluator;

      ...

          @Bean
          public PermissionEvaluator permissionEvaluator() {
              return new WildcardPermissionEvaluator();
          }

          @Bean
          public SecurityExpressionHandler methodSecurityExpressionHandler() {
              DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
              expressionHandler.setPermissionEvaluator(permissionEvaluator());
              return expressionHandler;
          }

          @Bean
          public SecurityExpressionHandler webSecurityExpressionHandler() {
              DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
              expressionHandler.setPermissionEvaluator(permissionEvaluator());
              return expressionHandler;
          }

  and then you can simply evaluate permissions this way using `Method Security Expressions <http://docs.spring.io/spring-security/site/docs/4.1.2.RELEASE/reference/html/el-access.html>`__:

  .. code:: java

      @PreAuthorize("hasPermission(...)")

  or using `JSP taglibs <http://docs.spring.io/spring-security/site/docs/4.1.x/reference/html/taglibs.html>`__

  .. code:: xml

      <sec:authorize access="hasPermission(...)" />

  That means, that if the ``jsmith`` account logs in, you can perform the following permission check:

  .. code:: java

      @PreAuthorize("hasPermission('user', 'login')")

  or

  .. code:: xml

      <sec:authorize access="hasPermission('user', 'login')" />

  And all this will return ``true``, because, while ``user:login`` isn't directly assigned to the account, it *is* assigned to one of the
  account's groups.

  Our ``PermissionEvaluator`` only customizes the way the ``hasPermissions`` operation behaves. The other Spring Security built-in
  expressions (e.g., hasRole(), isAnonymous(), isAuthenticated(), etc. are not modified). These expressions will carry out their usual operation:
  literal string comparisons. So, for example, if you want to check that a user has a specific role (in other words, it belongs to a specific
  Stormpath group) you can do:

  .. code:: java

      @PreAuthorize("hasAuthority('https://api.stormpath.com/v1/groups/upXiVIrPQ7yfA5L1G5ZaSQ')")

  The next sections cover the storage and retrieval details in case you're curious how it works, or if you'd like to customize the behavior or
  ``CustomData`` field name.

  Permission Storage
  ^^^^^^^^^^^^^^^^^^

  The ``CustomDataPermissionsEditor`` shown above, and the Spring Security Stormpath ``AuthenticationProvider`` default implementation assumes that
  a default field named ``springSecurityPermissions`` in an account's or group's ``CustomData`` resource can be used to store permissions
  assigned directly to the account or group. This implies the ``CustomData`` resource's JSON would look something like this:

  .. code:: json

      {
          "springSecurityPermissions": [
              "perm1",
              "perm2",
              "permN"
          ]
      }

  If you wanted to change the name to something else, you could specify the ``setFieldName`` property on the ``CustomDataPermissionsEditor``
  instance:

  .. code:: java

      new CustomDataPermissionsEditor(group.getCustomData())
          .setFieldName("whateverYouWantHere")
          .append("user:login");

  and this would result in the following JSON structure instead:

  .. code:: json

      {
          "whateverYouWantHere": [
              "user:login",
          ]
      }

  But *NOTE*: While the ``CustomDataPermissionsEditor`` implementation will modify the field name you specify, the, ``ApplicationRealm`` needs
  to read that same field during permission checks. So if you change it as shown above, you must also change the provider's configuration to
  reference the new name as well:

  .. code:: java

      import com.stormpath.spring.security.provider.AccountCustomDataPermissionResolver;
      import com.stormpath.spring.security.provider.GroupCustomDataPermissionResolver;
      import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;

      ...

          @Bean
          public GroupCustomDataPermissionResolver groupCustomDataPermissionResolver() {
              GroupCustomDataPermissionResolver permissionResolver = new GroupCustomDataPermissionResolver();
              permissionResolver.setCustomDataFieldName("whateverYouWantHere");
              return permissionResolver;
          }

          @Bean
          public AccountCustomDataPermissionResolver accountCustomDataPermissionResolver() {
              AccountCustomDataPermissionResolver permissionResolver = new AccountCustomDataPermissionResolver();
              permissionResolver.setCustomDataFieldName("whateverYouWantHere");
              return permissionResolver;
          }

          @Bean
          @Autowired
          public StormpathAuthenticationProvider stormpathAuthenticationProvider(Application application) {
              StormpathAuthenticationProvider authenticationProvider = new StormpathAuthenticationProvider(application);
              ...
              authenticationProvider.setGroupPermissionResolver(groupCustomDataPermissionResolver());
              authenticationProvider.setAccountPermissionResolver(accountCustomDataPermissionResolver());
              return authenticationProvider;
          }

  This section explained the default implementation strategy for storing and checking permissions, using Custom Data. You can use this
  immediately, as it is the default behavior, and it should suit 95% of all use cases.

  However, if you need another approach, you can fully customize how permissions are resolved for a given account or group by customizing the
  ``AuthorizationProvider``'s ``accountPermissionResolver`` and ``groupPermissionResolver`` properties.

  How Permission Checks Work
  ^^^^^^^^^^^^^^^^^^^^^^^^^^

  The Stormpath ``AuthenticationProvider`` will use any configured ``AccountPermissionResolver`` and ``GroupPermissionResolver``
  instances to create the aggregate of all permissions attributed to an ``Authorization``. Later on, these permissions will be
  evaluated when doing:

  .. code:: java

      @PreAuthorize("hasPermission('aPermission')")

  This operation will return ``true`` if the following is true:

  -  any of the permissions returned by the ``AccountPermissionResolver`` for the authorization's backing Account implies ``aPermission``
  -  any of the permissions returned by the ``GroupPermissionResolver`` for any of the backing Account's Groups implies ``aPermission``

  ``false`` will be returned if ``aPermission`` is not implied by any of these permissions.

  NOTE: pay attention that we are saying ``implies`` and not ``is equal to``. The ``implies(...)`` method is available through the
  ``Permission`` interface which extends Spring Security's ``GrantedAuthority``.
