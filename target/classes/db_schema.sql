CREATE TABLE spectrum_plots (
  id SERIAL ,
  wf_time TIMESTAMPTZ,
  spectrum_id INTEGER,
  wf_data JSON,
  wf_tag VARCHAR
);