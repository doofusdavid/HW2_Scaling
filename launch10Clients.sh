#!/usr/bin/env bash
cd out/production/HW2_Scaling

for i in `seq 1 10`;
        do
                java cs455.scaling.client.Client honeycrisp.local 8001 1 &
        done
