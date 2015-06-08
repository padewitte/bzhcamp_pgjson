CREATE EXTENSION plv8;


CREATE OR REPLACE FUNCTION json_data_update(data jsonb, field text,value text)
RETURNS jsonb
LANGUAGE plv8 STABLE STRICT
 AS $$
 var data = data;
 var val = value;
 data[field] = val;
 return JSON.stringify(data);
 $$;
 commit;


 CREATE EXTENSION plv8;


DROP FUNCTION json_data_update(data json, field text,value text)
RETURNS json;

CREATE OR REPLACE FUNCTION json_data_update(data json, field text,value text)
RETURNS json
LANGUAGE plv8 STABLE STRICT
 AS $$
 var data = data;
 var val = value;
 data['"'+field+'"'] = val;
 data[field] = val;
 var read = data[field]
 return data;
 $$;
 commit;




SELECT
data, json_data_update(data::json,
'price'::text,
'250'::text
) as old_price_data
FROM
json_data

select * from json_data