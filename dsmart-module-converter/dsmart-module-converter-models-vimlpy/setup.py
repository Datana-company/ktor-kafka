# coding: utf-8

import sys
from setuptools import setup, find_packages

NAME = "ru_datana_smart_ui_meta"
VERSION = "0.1.3"

# To install the library, run the following
#
# python setup.py install
#
# prerequisite: setuptools
# http://pypi.python.org/pypi/setuptools

REQUIRES = [
    "connexion>=2.0.2",
    "swagger-ui-bundle>=0.0.2",
    "python_dateutil>=2.6.0"
]

setup(
    name=NAME,
    version=VERSION,
    description="Форматы моделей для кислородного конвертера",
    author_email="okatov@datana.ru",
    url="",
    keywords=["OpenAPI", "Форматы моделей для кислородного конвертера"],
    install_requires=REQUIRES,
    packages=find_packages(),
    package_data={'': ['openapi/openapi.yaml']},
    include_package_data=True,
    entry_points={
        'console_scripts': ['ru_datana_smart_ui_meta=ru_datana_smart_ui_meta.__main__:main']},
    long_description="""\
    Транспортные модели для связи различных компонентов кислородного конвертера
    """
)

