<!DOCTYPE html>
<html>
<head>
    <title>FrontToBack Test</title>
    <meta name="viewport" content="initial-scale=1, user-scalable=no">
    <link rel="stylesheet" href="styles.css">
    <link rel="stylesheet" href="weather-icons.min.css">
    <script src="node_modules/es6-shim/es6-shim.min.js"></script>
    <script src="node_modules/systemjs/dist/system-polyfills.js"></script>
    <script src="node_modules/angular2/es6/dev/src/testing/shims_for_IE.js"></script>
    <script src="node_modules/angular2/bundles/angular2-polyfills.js"></script>
    <script src="node_modules/systemjs/dist/system-csp-production.src.js"></script>
    <script src="node_modules/rxjs/bundles/Rx.js"></script>
    <script src="node_modules/angular2/bundles/angular2.dev.js"></script>
</head>
<body>
<script language="JavaScript">
    'use strict';

    System.config({
        packages: {
            app: {
                format: 'register',
                defaultExtension: 'js'
            }
        }
    });
    System.import('app/main')
            .then(null, console.error.bind(console));

</script>
<test-app>Loading...</test-app></body>
</html>