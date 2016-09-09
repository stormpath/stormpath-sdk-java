#Stormpath Java Documentation

### Install

`pip install Sphinx sphinx_rtd_theme`

Other options [here](http://www.sphinx-doc.org/en/stable/install.html).

If you already have Sphinx, then just upgrade:

`pip install -U Sphinx sphinx_rtd_theme`

**NOTE:** Make sure you are using Sphinx 1.4.6 as a minimum. With previous versions, warnings are treated as errors.

Then clone this repo.

### Structure

```
├── source
    ├── _static
    ├── _templates
    ├── images
    │   ├── about
    │   ├── accnt_mgmt
    │   ├── auth_n
    │   ├── idsite
    │   ├── multitenancy
    │   └── quickstart
    └── robots
```

- All of the `.rst` files are in `source/`
- All images are in `source/images/`, sorted by chapter name.

### How To Generate The Docs

#### Generating Static Docs

`make html` will generate the Java SDK documentation as HTML.

`make html LANGUAGE={name}` will generate the documentation for the specified language.

The possible values for `{name}` are:

- `servlet`
- `springboot`

If you would like to generate all of the Product Guides in one go, you can use the same command that Travis uses:

`make allhtml`

This will iterate through every language and generate the Product Guide for that language.

**Note:** This command has an additional `-W` flag that converts all warnings into errors. This means that the build stops at the first warning.

#### Generating Live Docs

In order to generate auto-reloading "live" documentation, you'll need to install sphynx-autobuild:

`pip install sphinx-autobuild`

Then use the following command:

`make livehtml`

Just like `make html`, this command can also take a language parameter:

`make livehtml LANGUAGE={name}`

The values for `{name}` are the same as for generating static documentation.

### Viewing the Docs

Once you are finished generating the docs, you can view them with the following command:

`open build/html/index.html`

You can replace `index.html` with whatever chapter you would like.

If you used the `make allhtml` command, then you can find the generated files in:

`build/html/{language}/index.html`

