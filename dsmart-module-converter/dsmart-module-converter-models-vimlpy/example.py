import sys
from os.path import dirname

sys.path.append(dirname(__file__) + './build/generated')
for path in sys.path:
    print(path)

import json
from ru_datana_smart_ui_viml.encoder import JSONEncoder
from ru_datana_smart_ui_viml.models.converter_melt_info import ConverterMeltInfo
from ru_datana_smart_ui_viml.models.converter_transport_vi_ml import ConverterTransportViMl

viml = ConverterTransportViMl()
viml.melt_info = ConverterMeltInfo()
viml.melt_info.id = "123"
viml.melt_info.melt_number = "12312"

jsonStr = JSONEncoder().encode(viml)


def decode_hook(o):
    for key in o:
        if o[key] is None:
            o[key] = 'Cat'
    return o


print(jsonStr + "str")

viml1 = ConverterTransportViMl.from_dict(json.loads(jsonStr))
viml1.melt_info.melt_number = "34587987"
viml1.melt_info.steel_grade = "slkdj9879"

print(viml1)
