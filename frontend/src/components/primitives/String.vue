<template>
    <div>
        <div v-if="editMode" style="margin-top:-20px;">
            <div style="font-size: 16px; font-weight:700;">{{label}}</div>
            <v-text-field 
                v-model="value"
                @change="change"
                outlined
                style="width: 140px; min-height: 34px !important; border-radius: 0;"
            />
        </div>
        <div v-else>
            {{label}} : {{value}}
        </div>
    </div>
</template>
<script>  
    export default {
        name: 'String',
        components:{
        },
        props: {
            value:{
                type: String,
                default: ''
            },
            editMode: Boolean,
            label: String
        },
        methods:{
            change(){
                this.$emit("input", this.value);
            }
        }
    }
</script>
<style>
.v-text-field--outlined > .v-input__control > .v-input__slot{
    min-height: 34px !important;
}
</style>
