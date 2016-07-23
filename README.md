# Herman

Telegram bot

## Description 

Herman is an experimental [Telegram](https://telegram.org/) bot, which works with [DatomicDB](http://www.datomic.com/) as Storage and is
integrated with [Wordpress JSON API Plugin](https://wordpress.org/plugins/json-api/). 

## Installation

Download from https://github.com/lowl4tency/herman

## Usage

Herman is simple uberjar, compile it and run as java app

    $ lein uberjar
    $ java -jar herman-0.1.0-standalone.jar 

or
    $ lein with-profile app repl

## Compiling 

    $ lein with-profile app uberjar

## License

Copyright © 2016 Kirill Sotnikov

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
