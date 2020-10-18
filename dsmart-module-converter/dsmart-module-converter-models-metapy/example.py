import sys
from os.path import dirname

sys.path.append(dirname(__file__) + './build/generated')
for path in sys.path:
    print(path)

import json
from ru_datana_smart_ui_meta.encoder import JSONEncoder
from ru_datana_smart_ui_meta.models.converter_melt_info import ConverterMeltInfo

mi = ConverterMeltInfo()
mi.id = "123"
mi.melt_number = "12312"

jsonStr = JSONEncoder().encode(mi)


def decode_hook(o):
    for key in o:
        if o[key] is None:
            o[key] = 'Cat'
    return o


print(jsonStr + "str")

mi1 = ConverterMeltInfo.from_dict(json.loads(jsonStr))
mi1.melt_number = "34587987"
mi1.steel_grade = "slkdj9879"

print(mi1)
