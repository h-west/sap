<template>
  <div>
    <v-row>
    <v-col cols="12" sm="2" md="2">
      <v-menu
        ref="menu"
        v-model="sdtmenu"
        :close-on-content-click="false"
        :return-value.sync="date"
        transition="scale-transition"
        offset-y
        min-width="290px"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="sdt"
            label="Picker in menu"
            prepend-icon="mdi-calendar"
            readonly
            v-bind="attrs"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker v-model="sdt" no-title scrollable>
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="sdtmenu = false">확인</v-btn>
        </v-date-picker>
      </v-menu>
    </v-col>
    <v-spacer></v-spacer>
    <v-col cols="12" sm="2" md="2">
      <v-menu
        ref="menu"
        v-model="edtmenu"
        :close-on-content-click="false"
        :return-value.sync="date"
        transition="scale-transition"
        offset-y
        min-width="290px"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="edt"
            label="Picker in menu"
            prepend-icon="mdi-calendar"
            readonly
            v-bind="attrs"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker v-model="edt" no-title scrollable>
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="edtmenu = false">확인</v-btn>
        </v-date-picker>
      </v-menu>
    </v-col>
    <v-col cols="12" sm="6" md="6">
      매도 1,2
    </v-col>
    </v-row>
  </div>
</template>

<script>
export default {
  data: () => ({
      sdt: new Date().toISOString().substr(0, 10),
      edt: new Date().toISOString().substr(0, 10),
      sdtmenu: false,
      edtmenu: false,
      modal: false,
      menu2: false,
    }),
}

</script>