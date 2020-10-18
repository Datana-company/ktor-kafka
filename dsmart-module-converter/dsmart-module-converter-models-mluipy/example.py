import sys
from os.path import dirname

sys.path.append(dirname(__file__) + './build/generated')
for path in sys.path:
    print(path)

import json
from ru_datana_smart_ui_mlui.encoder import JSONEncoder
from ru_datana_smart_ui_mlui.models.converter_melt_info import ConverterMeltInfo
from ru_datana_smart_ui_mlui.models.converter_transport_ml_ui import ConverterTransportMlUi

mlui = ConverterTransportMlUi()
mlui.melt_info = ConverterMeltInfo()
mlui.melt_info.id = "123"
mlui.melt_info.melt_number = "12312"

jsonStr = JSONEncoder().encode(mlui)


def decode_hook(o):
    for key in o:
        if o[key] is None:
            o[key] = 'Cat'
    return o


print(jsonStr + "str")

mlui1 = ConverterTransportMlUi.from_dict(json.loads(jsonStr))
mlui1.melt_info.melt_number = "34587987"
mlui1.melt_info.steel_grade = "slkdj9879"

print(mlui1)
