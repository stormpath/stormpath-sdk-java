#Stormpath Java Documentation

### Install

#### Install SCMS

Download and install SCMS per these instructions: https://github.com/lhazlewood/scms#download-scms

(and yes, ensure `scms` is in your `$PATH`)

#### Install Sphinx

`pip install Sphinx sphinx_rtd_theme`

Other options [here](http://www.sphinx-doc.org/en/stable/install.html).

If you already have Sphinx, then just upgrade:

`pip install -U Sphinx sphinx_rtd_theme`

**NOTE:** Make sure you are using Sphinx 1.4.6 as a minimum. With previous versions, warnings are treated as errors.

#### Update Stormpath Theme Module

To pick up the shared Stormpath doc theme:

`git submodule update --init --recursive`

### Structure

```
├── source/
    ├── _static/
    ├── _themes/
    ├── appendix/
    ├── about.rst
    ├── ... etc ...
    └── views.rst
```

- All of the `.rst` files are in `source/`

### How To Generate The Docs

#### Generating Static Docs

In a `bash` shell:

```
./build.sh
```

This will generate all the integration guides we have.

If you want to generate just one guide, you can run:

```
./build.sh <name>
```

Where `<name>` is the unique identifier of the guide to generate.

The current possible values for `<name>` are:

- `servlet`
- `springboot`
- `sczuul`

### Viewing the Docs

Once you are finished generating the docs, you can view them with the following command:

`open build/<name>/build/html/index.html`

where, again, `<name>` is one of the above unique identifier names for the guide you want to view.

