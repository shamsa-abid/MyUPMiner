CREATE TABLE  api_call_index  (
   id  int(11) NOT NULL,
   api_call  varchar(255) NOT NULL
) ;

CREATE TABLE  cluster  (
   id  int(11) NOT NULL,
   clusterID  int(11) NOT NULL,
   sequenceID  int(11) NOT NULL,
   methodID  int(11) NOT NULL
);

CREATE TABLE  sequence  (
   id  int(11) NOT NULL,
   method_ID  int(11) NOT NULL,
   sequence  varchar(5000) NOT NULL
);

CREATE TABLE  sim_score  (
   id  int(11) NOT NULL,
   method_ID_1  int(11) NOT NULL,
   method_ID_2  int(11) NOT NULL,
   score  double NOT NULL
) ;

ALTER TABLE api_call_index
  ADD PRIMARY KEY (id);

ALTER TABLE api_call_index
  MODIFY id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
  
  ALTER TABLE cluster
  ADD PRIMARY KEY (id);

ALTER TABLE cluster
  MODIFY id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
  
  ALTER TABLE sequence
  ADD PRIMARY KEY (id);

ALTER TABLE sequence
  MODIFY id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
  
  ALTER TABLE sim_score
  ADD PRIMARY KEY (id);

ALTER TABLE sim_score
  MODIFY id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;