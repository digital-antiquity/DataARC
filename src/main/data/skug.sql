select array_to_json(array_agg(r)) from (select json_strip_nulls(row_to_json(t)) from (select id_row_tdar , autonumber , site , col_control , col_date , su , au_group ,( select h.phase from d_492517_skuggibased_mdb_phase h where h.phase=p.phase), ( select h.dates from d_492517_skuggibased_mdb_phase h where h.phase=p.phase),( select h.occupationperiod from d_492517_skuggibased_mdb_phase h where h.phase=p.phase) , area , context , x , y , z , other_context_1 , other_context_2 , context_notes , (select taxon from d_492517_skuggibased_mdb_bonecodes where species=code), (select trim(common) from d_492517_skuggibased_mdb_bonecodes where species=code) , (select distinct trim(commonname) from d_492517_skuggibased_mdb_boneelements where bonecode=p.bone limit 1) , (select trim(t.col_end) from d_492517_skuggibased_mdb_taphonomy t where t.endcode=p.col_end) , col_count , (select col_size from d_492517_skuggibased_mdb_fragmentsize where frag=sizecode) , col_zone , (select f.fusion_state from d_492517_skuggibased_mdb_fusionstate f where f.fusioncode=col_fusion) , (select trim(b.butchery) from d_492517_skuggibased_mdb_butchery b where b.butcherycode=p.butchery) , (select burning from d_492517_skuggibased_mdb_burning where burningcode=burn) , (select trim(g.gnawing) from d_492517_skuggibased_mdb_gnawing g where g.gnawingcode=p.gnaw ) , (select trim(a.age) from d_492517_skuggibased_mdb_age a where a.agecode=p.age ) , side , col_path , (select s.sex from d_492517_skuggibased_mdb_sex s where s.sexcode=p.sex) , ref_ , trim(col_comments) , ref__2 , dp4 , p4 , m1 , m2 , m3 , m3_p2 , m3_p4 , m3_m1 , m3l , m3b , bd , i_decid__ , sd , gl , bp , gb , bfd , bt , bfp , b , l , i_2 , mand_max , perm_decid , i1 , i2 , i3 , dc , dp2 , dp3 , i_1 , in__ , i_3 , col_c , p1 , p2 , glm , dm , gll , dl , fish_a_measurement , fish_b_measurement , p3 , postcanine , unit from d_492517_skuggibased_mdb_nabone2 p where phase='VI') t) r;

select array_to_json(array_agg(r)) from (select json_strip_nulls(row_to_json(t)) from (select id_row_tdar , autonumber , site , col_control , col_date , su , au_group ,( select h.phase from d_492517_skuggibased_mdb_phase h where h.phase=p.phase), ( select h.dates from d_492517_skuggibased_mdb_phase h where h.phase=p.phase),( select h.occupationperiod from d_492517_skuggibased_mdb_phase h where h.phase=p.phase) , area , context , x , y , z , other_context_1 , other_context_2 , context_notes , (select taxon from d_492517_skuggibased_mdb_bonecodes where species=code), (select trim(common) from d_492517_skuggibased_mdb_bonecodes where species=code) , (select distinct trim(commonname) from d_492517_skuggibased_mdb_boneelements where bonecode=p.bone limit 1) , (select trim(t.col_end) from d_492517_skuggibased_mdb_taphonomy t where t.endcode=p.col_end) , col_count , (select col_size from d_492517_skuggibased_mdb_fragmentsize where frag=sizecode) , col_zone , (select f.fusion_state from d_492517_skuggibased_mdb_fusionstate f where f.fusioncode=col_fusion) , (select trim(b.butchery) from d_492517_skuggibased_mdb_butchery b where b.butcherycode=p.butchery) , (select burning from d_492517_skuggibased_mdb_burning where burningcode=burn) , (select trim(g.gnawing) from d_492517_skuggibased_mdb_gnawing g where g.gnawingcode=p.gnaw ) , (select trim(a.age) from d_492517_skuggibased_mdb_age a where a.agecode=p.age ) , side , col_path , (select s.sex from d_492517_skuggibased_mdb_sex s where s.sexcode=p.sex) , ref_ , trim(col_comments) , ref__2 , dp4 , p4 , m1 , m2 , m3 , m3_p2 , m3_p4 , m3_m1 , m3l , m3b , bd , i_decid__ , sd , gl , bp , gb , bfd , bt , bfp , b , l , i_2 , mand_max , perm_decid , i1 , i2 , i3 , dc , dp2 , dp3 , i_1 , in__ , i_3 , col_c , p1 , p2 , glm , dm , gll , dl , fish_a_measurement , fish_b_measurement , p3 , postcanine , unit from d_492517_skuggibased_mdb_nabone2 p where phase='V') t) r;

select array_to_json(array_agg(r)) from (select json_strip_nulls(row_to_json(t)) from (select id_row_tdar , autonumber , site , col_control , col_date , su , au_group ,( select h.phase from d_492517_skuggibased_mdb_phase h where h.phase=p.phase), ( select h.dates from d_492517_skuggibased_mdb_phase h where h.phase=p.phase),( select h.occupationperiod from d_492517_skuggibased_mdb_phase h where h.phase=p.phase) , area , context , x , y , z , other_context_1 , other_context_2 , context_notes , (select taxon from d_492517_skuggibased_mdb_bonecodes where species=code), (select trim(common) from d_492517_skuggibased_mdb_bonecodes where species=code) , (select distinct trim(commonname) from d_492517_skuggibased_mdb_boneelements where bonecode=p.bone limit 1) , (select trim(t.col_end) from d_492517_skuggibased_mdb_taphonomy t where t.endcode=p.col_end) , col_count , (select col_size from d_492517_skuggibased_mdb_fragmentsize where frag=sizecode) , col_zone , (select f.fusion_state from d_492517_skuggibased_mdb_fusionstate f where f.fusioncode=col_fusion) , (select trim(b.butchery) from d_492517_skuggibased_mdb_butchery b where b.butcherycode=p.butchery) , (select burning from d_492517_skuggibased_mdb_burning where burningcode=burn) , (select trim(g.gnawing) from d_492517_skuggibased_mdb_gnawing g where g.gnawingcode=p.gnaw ) , (select trim(a.age) from d_492517_skuggibased_mdb_age a where a.agecode=p.age ) , side , col_path , (select s.sex from d_492517_skuggibased_mdb_sex s where s.sexcode=p.sex) , ref_ , trim(col_comments) , ref__2 , dp4 , p4 , m1 , m2 , m3 , m3_p2 , m3_p4 , m3_m1 , m3l , m3b , bd , i_decid__ , sd , gl , bp , gb , bfd , bt , bfp , b , l , i_2 , mand_max , perm_decid , i1 , i2 , i3 , dc , dp2 , dp3 , i_1 , in__ , i_3 , col_c , p1 , p2 , glm , dm , gll , dl , fish_a_measurement , fish_b_measurement , p3 , postcanine , unit from d_492517_skuggibased_mdb_nabone2 p where phase='IV') t) r;


select array_to_json(array_agg(r)) from (select json_strip_nulls(row_to_json(t)) from (select id_row_tdar , autonumber , site , col_control , col_date , su , au_group ,( select h.phase from d_492517_skuggibased_mdb_phase h where h.phase=p.phase), ( select h.dates from d_492517_skuggibased_mdb_phase h where h.phase=p.phase),( select h.occupationperiod from d_492517_skuggibased_mdb_phase h where h.phase=p.phase) , area , context , x , y , z , other_context_1 , other_context_2 , context_notes , (select taxon from d_492517_skuggibased_mdb_bonecodes where species=code), (select trim(common) from d_492517_skuggibased_mdb_bonecodes where species=code) , (select distinct trim(commonname) from d_492517_skuggibased_mdb_boneelements where bonecode=p.bone limit 1) , (select trim(t.col_end) from d_492517_skuggibased_mdb_taphonomy t where t.endcode=p.col_end) , col_count , (select col_size from d_492517_skuggibased_mdb_fragmentsize where frag=sizecode) , col_zone , (select f.fusion_state from d_492517_skuggibased_mdb_fusionstate f where f.fusioncode=col_fusion) , (select trim(b.butchery) from d_492517_skuggibased_mdb_butchery b where b.butcherycode=p.butchery) , (select burning from d_492517_skuggibased_mdb_burning where burningcode=burn) , (select trim(g.gnawing) from d_492517_skuggibased_mdb_gnawing g where g.gnawingcode=p.gnaw ) , (select trim(a.age) from d_492517_skuggibased_mdb_age a where a.agecode=p.age ) , side , col_path , (select s.sex from d_492517_skuggibased_mdb_sex s where s.sexcode=p.sex) , ref_ , trim(col_comments) , ref__2 , dp4 , p4 , m1 , m2 , m3 , m3_p2 , m3_p4 , m3_m1 , m3l , m3b , bd , i_decid__ , sd , gl , bp , gb , bfd , bt , bfp , b , l , i_2 , mand_max , perm_decid , i1 , i2 , i3 , dc , dp2 , dp3 , i_1 , in__ , i_3 , col_c , p1 , p2 , glm , dm , gll , dl , fish_a_measurement , fish_b_measurement , p3 , postcanine , unit from d_492517_skuggibased_mdb_nabone2 p where phase='III') t) r;

select array_to_json(array_agg(r)) from (select json_strip_nulls(row_to_json(t)) from (select id_row_tdar , autonumber , site , col_control , col_date , su , au_group ,( select h.phase from d_492517_skuggibased_mdb_phase h where h.phase=p.phase), ( select h.dates from d_492517_skuggibased_mdb_phase h where h.phase=p.phase),( select h.occupationperiod from d_492517_skuggibased_mdb_phase h where h.phase=p.phase) , area , context , x , y , z , other_context_1 , other_context_2 , context_notes , (select taxon from d_492517_skuggibased_mdb_bonecodes where species=code), (select trim(common) from d_492517_skuggibased_mdb_bonecodes where species=code) , (select distinct trim(commonname) from d_492517_skuggibased_mdb_boneelements where bonecode=p.bone limit 1) , (select trim(t.col_end) from d_492517_skuggibased_mdb_taphonomy t where t.endcode=p.col_end) , col_count , (select col_size from d_492517_skuggibased_mdb_fragmentsize where frag=sizecode) , col_zone , (select f.fusion_state from d_492517_skuggibased_mdb_fusionstate f where f.fusioncode=col_fusion) , (select trim(b.butchery) from d_492517_skuggibased_mdb_butchery b where b.butcherycode=p.butchery) , (select burning from d_492517_skuggibased_mdb_burning where burningcode=burn) , (select trim(g.gnawing) from d_492517_skuggibased_mdb_gnawing g where g.gnawingcode=p.gnaw ) , (select trim(a.age) from d_492517_skuggibased_mdb_age a where a.agecode=p.age ) , side , col_path , (select s.sex from d_492517_skuggibased_mdb_sex s where s.sexcode=p.sex) , ref_ , trim(col_comments) , ref__2 , dp4 , p4 , m1 , m2 , m3 , m3_p2 , m3_p4 , m3_m1 , m3l , m3b , bd , i_decid__ , sd , gl , bp , gb , bfd , bt , bfp , b , l , i_2 , mand_max , perm_decid , i1 , i2 , i3 , dc , dp2 , dp3 , i_1 , in__ , i_3 , col_c , p1 , p2 , glm , dm , gll , dl , fish_a_measurement , fish_b_measurement , p3 , postcanine , unit from d_492517_skuggibased_mdb_nabone2 p where phase='II') t) r;


select array_to_json(array_agg(r)) from (select json_strip_nulls(row_to_json(t)) from (select id_row_tdar , autonumber , site , col_control , col_date , su , au_group ,( select h.phase from d_492517_skuggibased_mdb_phase h where h.phase=p.phase), ( select h.dates from d_492517_skuggibased_mdb_phase h where h.phase=p.phase),( select h.occupationperiod from d_492517_skuggibased_mdb_phase h where h.phase=p.phase) , area , context , x , y , z , other_context_1 , other_context_2 , context_notes , (select taxon from d_492517_skuggibased_mdb_bonecodes where species=code), (select trim(common) from d_492517_skuggibased_mdb_bonecodes where species=code) , (select distinct trim(commonname) from d_492517_skuggibased_mdb_boneelements where bonecode=p.bone limit 1) , (select trim(t.col_end) from d_492517_skuggibased_mdb_taphonomy t where t.endcode=p.col_end) , col_count , (select col_size from d_492517_skuggibased_mdb_fragmentsize where frag=sizecode) , col_zone , (select f.fusion_state from d_492517_skuggibased_mdb_fusionstate f where f.fusioncode=col_fusion) , (select trim(b.butchery) from d_492517_skuggibased_mdb_butchery b where b.butcherycode=p.butchery) , (select burning from d_492517_skuggibased_mdb_burning where burningcode=burn) , (select trim(g.gnawing) from d_492517_skuggibased_mdb_gnawing g where g.gnawingcode=p.gnaw ) , (select trim(a.age) from d_492517_skuggibased_mdb_age a where a.agecode=p.age ) , side , col_path , (select s.sex from d_492517_skuggibased_mdb_sex s where s.sexcode=p.sex) , ref_ , trim(col_comments) , ref__2 , dp4 , p4 , m1 , m2 , m3 , m3_p2 , m3_p4 , m3_m1 , m3l , m3b , bd , i_decid__ , sd , gl , bp , gb , bfd , bt , bfp , b , l , i_2 , mand_max , perm_decid , i1 , i2 , i3 , dc , dp2 , dp3 , i_1 , in__ , i_3 , col_c , p1 , p2 , glm , dm , gll , dl , fish_a_measurement , fish_b_measurement , p3 , postcanine , unit from d_492517_skuggibased_mdb_nabone2 p where phase='I') t) r;