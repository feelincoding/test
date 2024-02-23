<template>
    <div>
        <div v-if="editMode" style="margin-top:-20px;">
            <div style="font-size: 16px; font-weight: 700;">{{label}}</div>
            <div style="position:relative; margin-left: -24px; left: 50%;">
                <v-switch
                    v-model="value"
                    hide-details
                    inset
                    @change="change"
                    style="margin-top: 0;"
                >
                </v-switch>
            </div>
        </div>
        <div v-else>
            {{label}} :  {{value}}
        </div>
    </div>
</template>
<script>
    export default {
        name: 'Boolean',
        components:{
        },
        props: {
            value:{
                type: Boolean,
                default: false
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
