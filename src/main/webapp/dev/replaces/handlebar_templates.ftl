  <script id="title-template-14" type="text/x-handlebars-template">
    <div class="title">
      {{data.0.site}}
    </div>
  </script>
  <script id="results-template-14" type="text/x-handlebars-template">
    <div class="description">
      <b>Site:</b> {{data.0.site}}<br>
<table class="table">
<tr>
<th>bone</th>
<th>species</th>
<th>unit</th>
<th>au</th>
<th>su</th>
<th>phase</th>
<th>age</th>
<th>sex</th>
<th>burn</th>

</tr>
{{#each this.data}}
  <tr>
    <td>{{bone}}</td>
    <td>{{species_comm}}</td>
    <td>{{unit}}</td>
    <td>{{au}}</td>
    <td>{{su}}</td>
    <td>{{phase}}</td>
    <td>{{age}}</td>
    <td>{{sex}}</td>
    <td>{{burn}}</td>
   </tr>
{{/each}}
</table>
    </div>
  </script>
  <script id="title-template-12" type="text/x-handlebars-template">
    <div class="title">
      {{orkneyfauna_site}} ({{orkneyfauna_islandregion}})
    </div>
  </script>
  <script id="results-template-12" type="text/x-handlebars-template">
    <div class="description">
      <b>{{orkneyfauna_site}} ({{orkneyfauna_island_or_region}})</b><br>
{{orkneyfauna_source}}
<br/>

<table class='table'>
<thead>
</thead>
<tr><td>Location:</td><td> {{orkneyfauna_location}}</td></tr>
<tr><td>Period:</td><td> {{orkneyfauna_period_code}} ({{orkneyfauna_period}}) </td></tr>
<tr><td>Date:</td><td> {{orkneyfauna_period_as_text}}</td></tr>


<tr><td>% Pig</td><td>{{orkneyfauna_percent_pig}}</td></tr>
<tr><td>% Sheep</td><td>{{orkneyfauna_percent_sheep}}</td></tr>
<tr><td>% Cow</td><td>{{orkneyfauna_percent_cow}}</td></tr>
<tr><td>% Marine Mammal</td><td>{{orkneyfauna_percent_marine_mammal}}</td></tr>
<tr><td>% Terrestrial Mammal</td><td>{{orkneyfauna_percent_terrestrial_mammal}}</td><?tr>
<tr><td></td><td>{{orkneyfauna_ab_sheepgoat_notes}}</td></tr>
<tr><td>% Pig To Bovids</td><td>{{orkneyfauna_percent_pig_to_bovids}}</td></tr>
<tr><td>% Sheep to Goat to Cow</td><td>{{ orkneyfauna_percent_sheep_to_goat_to_cow }}</td></tr>
<tr><td>NISP</td><td>{{orkneyfauna_nisp}}</td></tr>


</table>
    </div>
  </script>
  <script id="title-template-15" type="text/x-handlebars-template">
    <div class="title">
      {{sagas_name}} ({{sagas_saganame}} - {{sagas_chapter}})
    </div>
  </script>
  <script id="results-template-15" type="text/x-handlebars-template">
    <div class="description">
      <h3>{{sagas_name}}</h3>
<b>Saga:</b>{{sagas_saganame}}<br/>
<b>Chapter:</b> {{sagas_chapter}} (#{{sagas_chapternr}})<br/>
<b>Place Type:</b> {{sagas_type}}<br/>

<b>Date of saga action (dates from Complete Sagas of Icelanders):</b>{{sagas_action_start}}-{{sagas_action_end}}<br/>
<b>Posited date of composition (dates from V&eacute;steinn &Oacute;lason article in Blackwell Companion)</b>:{{sagas_composition_start}}-{{sagas_composition_end}}<br/>
<b>Oldest manuscript (according to Lethbridge 2014):</b> <a href="{{sagas_manuscript_link}}">{{sagas_oldest_manuscript}}</a> ({{sagas_oldest_manuscript_start}}-{{sagas_oldest_manuscript_end}})<br/>
    </div>
  </script>
  <script id="title-template-16" type="text/x-handlebars-template">
    <div class="title">
      {{teph_sitename}}

    </div>
  </script>
  <script id="results-template-16" type="text/x-handlebars-template">
    <div class="description">
      <b>Site:</b> {{teph_sitename}}<br>
<b>Profile: </b>{{teph_profilename}} (#{{teph_profilenumber}})
<br><br>
<table class="table">
<thead>
<tr><th>Tephra</th><th>Number</th><th>Year</th><th>Lower Depth</th><th>Upper Depth</th><th>Order</th></tr>
<thead>
<tbody>
{{#each layer1}}
<tr>
 <td> {{tephraname}} </td>
 <td> {{tephranumber}} </td>

 <td> {{tephrayear}} </td>
 <td> {{tephraldepth}} </td>

 <td> {{tephraudepth}} </td>
 <td> {{tephraorder}} </td>

</tr>
{{/each}}
</tbody>
</table>
    </div>
  </script>
  <script id="title-template-13" type="text/x-handlebars-template">
    <div class="title">
      {{iceland_farms_farm_name}} ({{iceland_farms_farm_number}})
    </div>
  </script>
  <script id="results-template-13" type="text/x-handlebars-template">
    <div class="description">
      <h4>Farm Name: {{iceland_farms_farm_name}}</h4>
<b>Parish:</b> {{iceland_farms_parish_name}}<br/>
<b>Shire:</b> {{iceland_farms_shire_name}}<br/>
<b>1861 Historic Value:</b>{{iceland_farms_historic_value_1861}}<br/>
<b>1861 Adjusted:</b>{{iceland_farms_adjusted_value_1861}}<br/>
    </div>
  </script>
  <script id="title-template-10" type="text/x-handlebars-template">
    <div class="title">
      {{sampledata.site_name}} - {{sampledata.site_id}} ({{sampledata.sample_name}})
    </div>
  </script>
  <script id="results-template-10" type="text/x-handlebars-template">
    <div class="description">
      <h3>{{sampledata.site_name}} - {{sampledata.sample_name}}</h3>
<p>
 {{#if sampledata}}
  <b>{{fieldName "country"}}:</b>{{country}}<br/>
  <b>{{fieldName "sampledata.age_name"}}:</b>{{sampledata.age_name}}<br/>
  <b>Date:</b>{{sampledata.start}} - {{sampledata.end}}<br/>
  {{/if}}
</p>
<table class="type-chart">
<thead><tr><th>Indicator</th><th>Value</th></tr></thead>
<tr><td>{{fieldName "indicators.dungfoul_habitats"}}</td><td>{{indicators.dungfoul_habitats}}</td></tr>
<tr><td>{{fieldName "indicators.carrion"}}</td><td>{{indicators.carrion}}</td></tr>
<tr><td>{{fieldName "indicators.open_wet_habitats"}}</td><td>{{indicators.open_wet_habitats}}</td></tr>
<tr><td>{{fieldName "indicators.heathland_moorland"}}</td><td>{{indicators.heathland_moorland}}</td></tr>
<tr><td>{{fieldName "indicators.wetlandsmarshes"}}</td><td>{{indicators.wetlandsmarshes}}</td></tr>
<tr><td>{{fieldName "indicators.indicators_dung"}}</td><td>{{indicators.indicators_dung}}</td></tr>
<tr><td>{{fieldName "indicators.aquatics"}}</td><td>{{indicators.aquatics}}</td></tr>
<tr><td>{{fieldName "indicators.meadowland"}}</td><td>{{indicators.meadowland}}</td></tr>
<tr><td>{{fieldName "indicators.dry_dead_wood"}}</td><td>{{indicators.dry_dead_wood}}</td></tr>
<tr><td>{{fieldName "indicators.halotolerant"}}</td><td>{{indicators.halotolerant}}</td></tr>
<tr><td>{{fieldName "indicators.general_synanthropic"}}</td><td>{{indicators.general_synanthropic}}</td></tr>
<tr><td>{{fieldName "indicators.indicators_deciduous"}}</td><td>{{indicators.indicators_deciduous}}</td></tr>
<tr><td>{{fieldName "indicators.sandydry_disturbedarable"}}</td><td>{{indicators.sandydry_disturbedarable}}</td></tr>
<tr><td>{{fieldName "indicators.ectoparasite"}}</td><td>{{indicators.ectoparasite}}</td></tr>
<tr><td>{{fieldName "indicators.pasturedung"}}</td><td>{{indicators.pasturedung}}</td></tr>
<tr><td>{{fieldName "indicators.indicators_coniferous"}}</td><td>{{indicators.indicators_coniferous}}</td></tr>
<tr><td>{{fieldName "indicators.disturbedarable"}}</td><td>{{indicators.disturbedarable}}</td></tr>
<tr><td>{{fieldName "indicators.indicators_standing_water"}}</td><td>{{indicators.indicators_standing_water}}</td></tr>
<tr><td>{{fieldName "indicators.wood_and_trees"}}</td><td>{{indicators.wood_and_trees}}</td></tr>
<tr><td>{{fieldName "indicators.stored_grain_pest"}}</td><td>{{indicators.stored_grain_pest}}</td></tr>
<tr><td>{{fieldName "indicators.indicators_running_water"}}</td><td>{{indicators.indicators_running_water}}</td></tr>
<tr><td>{{fieldName "indicators.mould_beetles"}}</td><td>{{indicators.mould_beetles}}</td></tr>
</table>
    </div>
  </script>
  <script id="title-template-11" type="text/x-handlebars-template">
    <div class="title">
      {{data.[0].site}}
    </div>
  </script>
  <script id="results-template-11" type="text/x-handlebars-template">
    <div class="description">
      <table class="table">
<thead>
<tr>
 <th>site</th>
 <th>b</th>
<th>b</th>
<th>bd</th>
<th>bfd</th>
<th>bfp</th>
<th>bp</th>
<th>bt</th>
<th>btrim</th>
<th>col_count</th>
<th>col_date</th>
<th>col_size</th>
<th>context</th>
<th>context_notes</th>
<th>dates</th>
<th>dp4</th>
<th>gb</th>
<th>gl</th>
<th>id</th>
<th>id_row_tdar</th>
<th>l</th>
<th>m1</th>
<th>m2</th>
<th>m3</th>
<th>occupationperiod</th>
<th>other_context_1</th>
<th>p4</th>
<th>phase</th>
<th>sd</th>
<th>su</th>
<th>taxon</th>
</tr>
</thead>
<tbody>
{{#each data}}
<tr>
 <td> {{site}} </td>
<td>{{b}}</td>
<td>{{bd}}</td>
<td>{{bfd}}</td>
<td>{{bfp}}</td>
<td>{{bp}}</td>
<td>{{bt}}</td>
<td>{{btrim}}</td>
<td>{{col_count}}</td>
<td>{{col_date}}</td>
<td>{{col_size}}</td>
<td>{{context}}</td>
<td>{{context_notes}}</td>
<td>{{dates}}</td>
<td>{{dp4}}</td>
<td>{{gb}}</td>
<td>{{gl}}</td>
<td>{{id}}</td>
<td>{{id_row_tdar}}</td>
<td>{{l}}</td>
<td>{{m1}}</td>
<td>{{m2}}</td>
<td>{{m3}}</td>
<td>{{occupationperiod}}</td>
<td>{{other_context_1}}</td>
<td>{{p4}}</td>
<td>{{phase}}</td>
<td>{{sd}}</td>
<td>{{site}}</td>
<td>{{su}}</td>
<td>{{taxon}}</td>
</tr>
{{/each}}
</tbody>
</table>
    </div>
  </script>
  <script id="title-template-17" type="text/x-handlebars-template">
    <div class="title">
      {{viga_name}} ({{viga_saganame}})
    </div>
  </script>
  <script id="results-template-17" type="text/x-handlebars-template">
    <div class="description">
      <p>
<b>Saga:</b> {{viga_saganame}}<br>
<b>Chapter:</b> {{viga_chapter}}<br>
<b>Name:</b> {{viga_name}}<br>
<b>Text:</b> {{viga_text}}<br>
<b>Concept:</b> {{viga_concept}}<br>
    </div>
  </script>
  <div class="hidden">
    <div id="nabone_svk_bio">Test NABONE data from Sveigakot in Northern Iceland.</div>
    <div id="orkneyfauna_bio">Test archaeological faunal data from the Orkney Islands, Scotland.</div>
    <div id="sagas_bio">The Icelandic Saga Map is intended as a resource to guide specialists and non-specialists alike around the Islendingasogur from a spatial perspective. On it, places named in the sagas are hyperlinked to their occurrences in the saga texts. There are also links to images of places and to other sources of information concerning them (e.g., problems with respect to their identification in modern-day Icelandic landscapes, details about archaeological excavations).</div>
    <div id="teph_bio">Tephra (volcanic ash) layers are now an invaluable tool in palaeoenvironmental studies, as well a record of volcanic activity. The data produced by such research can be difficult to handle and disseminate. Tephrabase is a database of tephra layers found in Iceland, north-west and northern Europe, Russia and central Mexico. Details on the location, name, age and geochemistry of tephra layers are stored in the database, as well as information about relevant volcanoes and volcanic systems. A comprehensive reference database is also included.</div>
    <div id="iceland_farms_bio">The project aims to make available the most detailed historical documents describing the way land was used in late medieval to early modern Iceland, roughly 1500-1860. The central document is the early 18th century land census Jardabok Arna Magnussonar og Pals Vidalin, widely regarded as one of the most important documents ever produced about Icelandic agriculture. The data include information about every main farm (e.g., 'logbyli') recorded in the land census and contains a large amount of categorical and quantitative data. In addition, several networks of interaction show the interconnectedness of farms at the time. These connecting lines are sometimes reciprocal, sometimes a manifestation of material and political inequality, but all of them indicate the entangled character of Iceland's agricultural communities.</div>
    <div id="sead_bio">The Strategic Environmental Archaeology Database (SEAD) is a research infrastructure for storing, manipulating and analyzing proxy data from archaeological and palaeoenvironmental investigations. The primary objectives of SEAD are to make environmental archaeology data available to the international research community and to provide online tools to assist in the analysis of these data.</div>
    <div id="nabone_bio">The NABONE dataset is a set of databases of archaeological fauna from sites across the North Atlantic, including Iceland, Greenland, and the Faroe Islands. The NABONE zooarchaeological analysis package has been under development since January 1997, when a group of 22 zooarchaeologists from Canada, US, UK, and Scandinavia specializing in North Atlantic collections met in New York as part of a US NSF-funded effort to improve data comparability and curation in this important area.</div>
    <div id="viga_bio">This is a test data-source which attempts to map concepts into the Viga Saga</div>
  </div>